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
import models.journeyDomain._
import models.reference.IncidentCode._
import models.{Index, RichJsArray, UserAnswers}
import pages.incident.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.Section
import pages.sections.incident.EquipmentsSection

case class EquipmentsDomain(
  value: Seq[EquipmentDomain]
)(incidentIndex: Index)
    extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[?]] = Some(EquipmentsSection(incidentIndex))
}

object EquipmentsDomain {

  // scalastyle:off cyclomatic.complexity
  implicit def userAnswersReader(incidentIndex: Index): Read[EquipmentsDomain] = {
    lazy val readEquipments: Read[EquipmentsDomain] =
      EquipmentsSection(incidentIndex).arrayReader
        .to {
          case x if x.isEmpty =>
            EquipmentDomain.userAnswersReader(incidentIndex, Index(0)).toSeq
          case x =>
            x.traverse[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, _).apply(_))
        }
        .map(EquipmentsDomain(_)(incidentIndex))

    IncidentCodePage(incidentIndex).reader.to {
      _.code match {
        case TransferredToAnotherTransportCode | UnexpectedlyChangedCode =>
          ContainerIndicatorYesNoPage(incidentIndex).reader.to {
            case true =>
              readEquipments
            case false =>
              AddTransportEquipmentPage(incidentIndex).reader.to {
                case true  => readEquipments
                case false => UserAnswersReader.success(EquipmentsDomain(Nil)(incidentIndex))
              }
          }
        case SealsBrokenOrTamperedCode | PartiallyOrFullyUnloadedCode =>
          readEquipments
        case _ =>
          UserAnswersReader.success(EquipmentsDomain(Nil)(incidentIndex))
      }
    }
  }
  // scalastyle:on cyclomatic.complexity
}
