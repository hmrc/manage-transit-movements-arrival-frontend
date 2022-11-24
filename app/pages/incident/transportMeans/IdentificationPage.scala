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

package pages.incident.transportMeans

import controllers.incident.transportMeans.routes
import models.incident.transportMeans.Identification
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.incident.TransportMeansSection
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class IdentificationPage(index: Index) extends QuestionPage[Identification] {

  override def path: JsPath = TransportMeansSection(index).path \ toString

  override def toString: String = "identification"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.IdentificationController.onPageLoad(userAnswers.mrn, mode, index))

  override def cleanup(value: Option[Identification], userAnswers: UserAnswers): Try[UserAnswers] = value match {
    case Some(_) => userAnswers.remove(IdentificationNumberPage(index)).flatMap(_.remove(TransportNationalityPage(index)))
    case None    => super.cleanup(value, userAnswers)
  }
}
