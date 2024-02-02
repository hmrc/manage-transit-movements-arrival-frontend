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

import config.Constants.IncidentCode._
import controllers.incident.equipment.routes
import models.journeyDomain.incident.equipment.itemNumber.ItemNumbersDomain
import models.journeyDomain.incident.equipment.seal.SealsDomain
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Read, Stage, UserAnswersReader}
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
  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): Read[EquipmentDomain] = {
    lazy val sealsReads: Read[SealsDomain] =
      SealsDomain.userAnswersReader(incidentIndex, equipmentIndex)

    lazy val itemNumbersReads: Read[ItemNumbersDomain] =
      ItemNumbersDomain.userAnswersReader(incidentIndex, equipmentIndex)

    lazy val optionalSealsReads: Read[SealsDomain] =
      AddSealsYesNoPage(incidentIndex, equipmentIndex).reader.to {
        case true  => sealsReads
        case false => UserAnswersReader.success(SealsDomain(Nil))
      }

    lazy val optionalItemNumbersReads: Read[ItemNumbersDomain] =
      AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex).reader.to {
        case true  => itemNumbersReads
        case false => UserAnswersReader.success(ItemNumbersDomain(Nil))
      }

    lazy val sealsReadsByIncidentCode: Read[SealsDomain] =
      IncidentCodePage(incidentIndex).reader.to {
        _.code match {
          case SealsBrokenOrTamperedCode => sealsReads
          case _                         => optionalSealsReads
        }
      }

    lazy val readsWithContainerId: Read[EquipmentDomain] = (
      ContainerIdentificationNumberPage(incidentIndex, equipmentIndex).reader.toOption,
      sealsReadsByIncidentCode,
      optionalItemNumbersReads
    ).map(EquipmentDomain.apply(_, _, _)(incidentIndex, equipmentIndex))

    lazy val readsWithOptionalContainerId: Read[EquipmentDomain] =
      ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex).reader.to {
        case true =>
          readsWithContainerId
        case false =>
          (
            UserAnswersReader.none[String],
            sealsReads,
            optionalItemNumbersReads
          ).map(EquipmentDomain.apply(_, _, _)(incidentIndex, equipmentIndex))
      }

    IncidentCodePage(incidentIndex).reader.to {
      _.code match {
        case TransferredToAnotherTransportCode | UnexpectedlyChangedCode =>
          ContainerIndicatorYesNoPage(incidentIndex).reader.to {
            case true  => readsWithContainerId
            case false => readsWithOptionalContainerId
          }
        case SealsBrokenOrTamperedCode | PartiallyOrFullyUnloadedCode =>
          readsWithOptionalContainerId
        case _ =>
          UserAnswersReader.error(IncidentCodePage(incidentIndex))
      }
    }
  }

  // scalastyle:on cyclomatic.complexity
  // scalastyle:on method.length
}
