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

package pages.incident.endorsement

import controllers.incident.endorsement.routes
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.incident.EndorsementSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import java.time.LocalDate

case class EndorsementDatePage(index: Index) extends QuestionPage[LocalDate] {

  override def path: JsPath = EndorsementSection(index).path \ toString

  override def toString: String = "date"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.EndorsementDateController.onPageLoad(userAnswers.mrn, index, mode))
}
