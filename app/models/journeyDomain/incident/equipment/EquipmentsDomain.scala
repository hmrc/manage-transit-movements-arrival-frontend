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

import controllers.incident.equipment.routes
import models.incident.IncidentCode._
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, JsArrayGettableAsReaderOps, Stage, UserAnswersReader}
import models.{Index, Mode, RichJsArray, UserAnswers}
import pages.incident.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.incident.EquipmentsSection
import play.api.mvc.Call

case class EquipmentsDomain(equipments: Seq[EquipmentDomain])(incidentIndex: Index) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.AddAnotherEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex))
}

object EquipmentsDomain {

  // scalastyle:off cyclomatic.complexity
  implicit def userAnswersReader(incidentIndex: Index): UserAnswersReader[EquipmentsDomain] = {
    lazy val readEquipments: UserAnswersReader[EquipmentsDomain] =
      EquipmentsSection(incidentIndex).reader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, Index(0)))
            .map(Seq(_))
            .map(EquipmentsDomain(_)(incidentIndex))
        case x =>
          x.traverse[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, _))
            .map(EquipmentsDomain(_)(incidentIndex))
      }

    IncidentCodePage(incidentIndex).reader.flatMap {
      case TransferredToAnotherTransport | UnexpectedlyChanged =>
        ContainerIndicatorYesNoPage(incidentIndex).reader.flatMap {
          case true => readEquipments
          case false =>
            AddTransportEquipmentPage(incidentIndex).reader.flatMap {
              case true  => readEquipments
              case false => UserAnswersReader(EquipmentsDomain(Nil)(incidentIndex))
            }
        }
      case SealsBrokenOrTampered | PartiallyOrFullyUnloaded => readEquipments
      case DeviatedFromItinerary | CarrierUnableToComply    => UserAnswersReader(EquipmentsDomain(Nil)(incidentIndex))
    }
  }
  // scalastyle:on cyclomatic.complexity
}
