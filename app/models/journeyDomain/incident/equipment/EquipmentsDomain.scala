/*
 * Copyright 2022 HM Revenue & Customs
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

import models.incident.IncidentCode._
import models.journeyDomain.{GettableAsReaderOps, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, RichJsArray}
import pages.QuestionPage
import pages.incident.equipment.{ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import pages.incident.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.incident.EquipmentsSection

case class EquipmentsDomain(equipments: Seq[EquipmentDomain])(incidentIndex: Index)

object EquipmentsDomain {

  implicit def userAnswersReader(incidentIndex: Index): UserAnswersReader[EquipmentsDomain] = {
    def readEquipmentsOrRedirectToPage[T](page: (Index, Index) => QuestionPage[T]): UserAnswersReader[EquipmentsDomain] =
      EquipmentsSection(incidentIndex).reader.flatMap {
        case x if x.isEmpty => UserAnswersReader.fail(page(incidentIndex, Index(0)))
        case x              => x.traverse[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, _)).map(EquipmentsDomain.apply(_)(incidentIndex))
      }

    IncidentCodePage(incidentIndex).reader.flatMap {
      case TransferredToAnotherTransport | UnexpectedlyChanged =>
        ContainerIndicatorYesNoPage(incidentIndex).reader.flatMap {
          case true => readEquipmentsOrRedirectToPage(ContainerIdentificationNumberPage)
          case false =>
            AddTransportEquipmentPage(incidentIndex).reader.flatMap {
              case true  => readEquipmentsOrRedirectToPage(ContainerIdentificationNumberYesNoPage)
              case false => UserAnswersReader(EquipmentsDomain(Nil)(incidentIndex))
            }
        }
      case SealsBrokenOrTampered | PartiallyOrFullyUnloaded => readEquipmentsOrRedirectToPage(ContainerIdentificationNumberYesNoPage)
      case DeviatedFromItinerary | CarrierUnableToComply    => UserAnswersReader(EquipmentsDomain(Nil)(incidentIndex))
    }
  }
}