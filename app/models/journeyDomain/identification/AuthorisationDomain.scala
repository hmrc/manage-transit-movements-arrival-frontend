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

package models.journeyDomain.identification

import cats.implicits._
import config.FrontendAppConfig
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

  override def toString: String = s"${`type`.toString} - $referenceNumber"

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage)(implicit config: FrontendAppConfig): Option[Call] =
    Some(controllers.identification.authorisation.routes.CheckAuthorisationAnswersController.onPageLoad(userAnswers.mrn, index))
}

object AuthorisationDomain {

  implicit def userAnswersReader(index: Index): UserAnswersReader[AuthorisationDomain] =
    (
      AuthorisationTypePage(index).reader,
      AuthorisationReferenceNumberPage(index).reader
    ).mapN {
      (`type`, referenceNumber) => AuthorisationDomain(`type`, referenceNumber)(index)
    }
}
