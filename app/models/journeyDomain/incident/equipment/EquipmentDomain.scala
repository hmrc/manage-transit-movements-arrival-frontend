/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.journeyDomain.incident.equipment

import cats.implicits._
import config.Constants.IncidentCode._
import controllers.incident.equipment.routes
import models.journeyDomain.incident.equipment.itemNumber.ItemNumbersDomain
import models.journeyDomain.incident.equipment.seal.SealsDomain
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.reference.IncidentCode._
import models.{Index, Mode, UserAnswers}
import pages.incident.equipment._
import pages.incident.{ContainerIndicatorYesNoPage, IncidentCodePage}
import play.api.i18n.Messages
import play.api.mvc.Call

case class EquipmentDomain(
  containerId: Option[String],
  seals: SealsDomain,
  itemNumbers: ItemNumbersDomain
)(incidentIndex: Index, equipmentIndex: Index)
    extends JourneyDomainModel {

  def asString(implicit messages: Messages): String = EquipmentDomain.asString(containerId, equipmentIndex)

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex))
}

object EquipmentDomain {

  def asString(containerId: Option[String], equipmentIndex: Index)(implicit messages: Messages): String = containerId match {
    case Some(value) => messages("incident.equipment.withContainer.label", equipmentIndex.display, value)
    case None        => messages("incident.equipment.withoutContainer.label", equipmentIndex.display)
  }

  // scalastyle:off cyclomatic.complexity
  // scalastyle:off method.length
  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[EquipmentDomain] = {
    lazy val sealsReads       = UserAnswersReader[SealsDomain](SealsDomain.userAnswersReader(incidentIndex, equipmentIndex))
    lazy val itemNumbersReads = UserAnswersReader[ItemNumbersDomain](ItemNumbersDomain.userAnswersReader(incidentIndex, equipmentIndex))

    lazy val optionalSealsReads = AddSealsYesNoPage(incidentIndex, equipmentIndex).reader.flatMap {
      case true  => sealsReads
      case false => UserAnswersReader.apply(SealsDomain(Nil))
    }

    lazy val optionalItemNumbersReads = AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex).reader.flatMap {
      case true  => itemNumbersReads
      case false => UserAnswersReader.apply(ItemNumbersDomain(Nil))
    }

    lazy val sealsReadsByIncidentCode: UserAnswersReader[SealsDomain] = IncidentCodePage(incidentIndex).reader.map(_.code).flatMap {
      case SealsBrokenOrTamperedCode => sealsReads
      case _                         => optionalSealsReads
    }

    lazy val readsWithContainerId: UserAnswersReader[EquipmentDomain] = (
      ContainerIdentificationNumberPage(incidentIndex, equipmentIndex).reader.map(Some(_)),
      sealsReadsByIncidentCode,
      optionalItemNumbersReads
    ).tupled.map((EquipmentDomain.apply _).tupled).map(_(incidentIndex, equipmentIndex))

    lazy val readsWithOptionalContainerId: UserAnswersReader[EquipmentDomain] =
      ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex).reader.flatMap {
        case true => readsWithContainerId
        case false =>
          (
            UserAnswersReader[Option[String]](None),
            sealsReads,
            optionalItemNumbersReads
          ).tupled.map((EquipmentDomain.apply _).tupled).map(_(incidentIndex, equipmentIndex))
      }

    IncidentCodePage(incidentIndex).reader.map(_.code).flatMap {

      case TransferredToAnotherTransportCode | UnexpectedlyChangedCode =>
        ContainerIndicatorYesNoPage(incidentIndex).reader.flatMap {
          case true  => readsWithContainerId
          case false => readsWithOptionalContainerId
        }
      case SealsBrokenOrTamperedCode | PartiallyOrFullyUnloadedCode => readsWithOptionalContainerId
      case _                                                        => UserAnswersReader.fail(IncidentCodePage(incidentIndex))

    }
  }

  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length
}
