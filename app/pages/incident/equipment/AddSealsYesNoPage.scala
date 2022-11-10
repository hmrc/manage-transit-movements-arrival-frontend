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

package pages.incident.equipment

import controllers.incident.equipment.routes
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.incident.{EquipmentSection, SealsSection}
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class AddSealsYesNoPage(incidentIndex: Index, equipmentIndex: Index) extends QuestionPage[Boolean] {

  override def path: JsPath = EquipmentSection(incidentIndex, equipmentIndex).path \ toString

  override def toString: String = "addSealsYesNo"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.AddSealsYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex))

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) => userAnswers.remove(SealsSection(incidentIndex, equipmentIndex))
      case _           => super.cleanup(value, userAnswers)
    }

}
