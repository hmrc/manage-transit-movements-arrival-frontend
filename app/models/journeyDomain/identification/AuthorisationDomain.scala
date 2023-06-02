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

package models.journeyDomain.identification

import cats.implicits._
import controllers.identification.authorisation.routes
import controllers.locationOfGoods.routes.TypeOfLocationController
import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
//import controllers.identification.authorisation.routes
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.{Index, Mode, UserAnswers}
import pages.identification.authorisation._
import play.api.mvc.Call

case class AuthorisationDomain(
  `type`: AuthorisationType,
  referenceNumber: String
)(index: Index)
    extends JourneyDomainModel {

  def asString(f: String => AuthorisationType => String): String =
    s"${`type`.asString(f)} - $referenceNumber"

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    //    Some(routes.AuthorisationReferenceNumberController.onPageLoad(userAnswers.mrn, index, mode))
    //    Some(routes.CheckAuthorisationAnswersController.onPageLoad(userAnswers.mrn, index, mode))
    stage match {
      case AccessingJourney => Some(routes.AuthorisationReferenceNumberController.onPageLoad(userAnswers.mrn, index, mode))
      case CompletingJourney =>
        Some(TypeOfLocationController.onPageLoad(userAnswers.mrn, mode))
    }
}

object AuthorisationDomain {

  implicit def userAnswersReader(index: Index): UserAnswersReader[AuthorisationDomain] =
    (
      AuthorisationTypePage(index).reader,
      AuthorisationReferenceNumberPage(index).reader
    ).tupled.map((AuthorisationDomain.apply _).tupled).map(_(index))
}
