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

import models.Index
import models.journeyDomain.{JourneyDomainModel, UserAnswersReader}

case class EquipmentDomain(containerId: Option[String]) extends JourneyDomainModel

object EquipmentDomain {

  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[EquipmentDomain] =
    UserAnswersReader.apply(EquipmentDomain(None))

  /*def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[Option[EquipmentDomain]] =
    IncidentCodePage(incidentIndex).reader.flatMap {
      case SealsBrokenOrTampered | PartiallyOrFullyUnloaded =>
        ContainerIdentificationNumberYesNoPage(incidentIndex)
          .filterOptionalDependent(identity)(ContainerIdentificationNumberPage(incidentIndex).reader)
          .map(EquipmentDomain(_))
          .map(Some(_))
      case TransferredToAnotherTransport | UnexpectedlyChanged =>
        ContainerIndicatorYesNoPage(incidentIndex).reader.flatMap {
          case true =>
            ContainerIdentificationNumberPage(incidentIndex).reader.map(Some(_)).map(EquipmentDomain(_)).map(Some(_))
          case false =>
            AddTransportEquipmentPage(incidentIndex)
              .filterOptionalDependent(identity) {
                ContainerIdentificationNumberYesNoPage(incidentIndex).filterOptionalDependent(identity) {
                  ContainerIdentificationNumberPage(incidentIndex).reader
                }
              }
              .map(_.flatten)
              .map(EquipmentDomain(_))
              .map(Some(_))
        }
      case DeviatedFromItinerary | CarrierUnableToComply =>
        UserAnswersReader.apply(None)
    }*/
}
