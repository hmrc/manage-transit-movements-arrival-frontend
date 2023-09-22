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

package pages.locationOfGoods

import controllers.locationOfGoods.routes
import models.reference.QualifierOfIdentification
import models.{Mode, UserAnswers}
import pages.QuestionPage
import pages.sections.locationOfGoods.{ContactPersonSection, LocationOfGoodsSection, QualifierOfIdentificationDetailsSection}
import play.api.libs.json.JsPath
import play.api.mvc.Call
import config.Constants._

import scala.util.{Success, Try}

case object QualifierOfIdentificationPage extends QuestionPage[QualifierOfIdentification] {

  override def path: JsPath = LocationOfGoodsSection.path \ toString

  override def toString: String = "qualifierOfIdentification"

  override def route(userAnswers: UserAnswers, mode: Mode): Option[Call] =
    Some(routes.QualifierOfIdentificationController.onPageLoad(userAnswers.mrn, mode))

  override def cleanup(value: Option[QualifierOfIdentification], userAnswers: UserAnswers): Try[UserAnswers] = {

    def removeContactPerson(value: QualifierOfIdentification, userAnswers: UserAnswers): Try[UserAnswers] =
      value.code match {
        case CustomsOfficeCode => userAnswers.remove(AddContactPersonPage).flatMap(_.remove(ContactPersonSection))
        case _                 => Success(userAnswers)
      }

    value match {
      case Some(value) => userAnswers.remove(QualifierOfIdentificationDetailsSection).flatMap(removeContactPerson(value, _))
      case _           => super.cleanup(value, userAnswers)
    }
  }
}
