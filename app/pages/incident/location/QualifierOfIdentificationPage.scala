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

package pages.incident.location

import controllers.incident.location.routes
import forms.Constants._
import models.reference.QualifierOfIdentification
import models.{Index, Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.incident
import play.api.libs.json.JsPath
import play.api.mvc.Call

import scala.util.Try

case class QualifierOfIdentificationPage(index: Index) extends QuestionPage[QualifierOfIdentification] {

  override def path: JsPath = incident.IncidentSection(index).path \ toString

  override def toString: String = "qualifierOfIdentification"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.QualifierOfIdentificationController.onPageLoad(userAnswers.mrn, mode, index))

  override def cleanup(value: Option[QualifierOfIdentification], userAnswers: UserAnswers): Try[UserAnswers] =
    value.map(_.code) match {
      case Some(CoordinatesCode) =>
        userAnswers
          .remove(UnLocodePage(index))
          .flatMap(_.remove(AddressPage(index)))
      case Some(UnlocodeCode) =>
        userAnswers
          .remove(CoordinatesPage(index))
          .flatMap(_.remove(AddressPage(index)))
      case Some(AddressCode) =>
        userAnswers
          .remove(CoordinatesPage(index))
          .flatMap(_.remove(UnLocodePage(index)))
      case _ => super.cleanup(value, userAnswers)
    }
}
