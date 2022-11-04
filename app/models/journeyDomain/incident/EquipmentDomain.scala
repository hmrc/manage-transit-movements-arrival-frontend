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

package models.journeyDomain.incident

import models.Index
import models.incident.IncidentCode._
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import pages.incident.IncidentCodePage
import pages.incident.equipment._

case class EquipmentDomain(containerId: Option[String])

object EquipmentDomain {

  def userAnswersReader(index: Index): UserAnswersReader[EquipmentDomain] =
    IncidentCodePage(index).reader.flatMap {
      case SealsBrokenOrTampered | PartiallyOrFullyUnloaded =>
        ContainerIdentificationNumberYesNoPage(index)
          .filterOptionalDependent(identity)(ContainerIdentificationNumberPage(index).reader)
          .map(EquipmentDomain(_))
      case TransferredToAnotherTransport | UnexpectedlyChanged =>
        ContainerIndicatorYesNoPage(index).reader.flatMap {
          case true =>
            ContainerIdentificationNumberPage(index).reader.map(Some(_)).map(EquipmentDomain(_))
          case false =>
            AddTransportEquipmentPage(index)
              .filterOptionalDependent(identity) {
                ContainerIdentificationNumberYesNoPage(index).filterOptionalDependent(identity) {
                  ContainerIdentificationNumberPage(index).reader
                }
              }
              .map(_.flatten)
              .map(EquipmentDomain(_))
        }
      case DeviatedFromItinerary | CarrierUnableToComply =>
        UserAnswersReader.apply(EquipmentDomain(None))
    }
}
