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

package pages.incident.equipment.seal

import controllers.incident.equipment.seal.routes
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.incident.SealSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

case class SealIdentificationNumberPage(incidentIndex: Index, sealIndex: Index) extends QuestionPage[String] {

  override def path: JsPath = SealSection(incidentIndex, sealIndex).path \ toString

  override def toString: String = "sealIdentificationNumber"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, sealIndex))
}
