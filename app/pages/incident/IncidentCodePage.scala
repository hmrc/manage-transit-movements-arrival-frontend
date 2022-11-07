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

package pages.incident

import controllers.incident.routes
import models.incident.IncidentCode
import models.incident.IncidentCode._
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.incident.equipment.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage}
import pages.sections.incident
import pages.sections.incident.EquipmentSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class IncidentCodePage(index: Index) extends QuestionPage[IncidentCode] {

  override def path: JsPath = incident.IncidentSection(index).path \ toString

  override def toString: String = "incidentCode"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.IncidentCodeController.onPageLoad(userAnswers.mrn, mode, index))

  override def cleanup(value: Option[IncidentCode], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(SealsBrokenOrTampered | PartiallyOrFullyUnloaded) =>
        userAnswers
          .remove(ContainerIndicatorYesNoPage(index))
          .flatMap(_.remove(AddTransportEquipmentPage(index)))
      case Some(DeviatedFromItinerary | CarrierUnableToComply) =>
        userAnswers.remove(EquipmentSection(index))
      case _ => super.cleanup(value, userAnswers)
    }
}
