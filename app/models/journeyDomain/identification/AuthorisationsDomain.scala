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
import controllers.identification.authorisation.routes
import models.journeyDomain.{JourneyDomainModel, JsArrayGettableAsReaderOps, Stage, UserAnswersReader}
import models.{Index, Mode, RichJsArray, UserAnswers}
import pages.identification.authorisation.AuthorisationTypePage
import pages.sections.AuthorisationsSection
import play.api.mvc.Call

case class AuthorisationsDomain(
  value: Seq[AuthorisationDomain] // TODO this could be a nonEmptyList
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage)(implicit config: FrontendAppConfig): Option[Call] =
    Some(routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.mrn))
}

object AuthorisationsDomain {

  implicit val userAnswersReader: UserAnswersReader[AuthorisationsDomain] =
    AuthorisationsSection.reader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader.fail[AuthorisationsDomain](AuthorisationTypePage(Index(0)))
      case x =>
        x.traverse[AuthorisationDomain](AuthorisationDomain.userAnswersReader).map(AuthorisationsDomain.apply)
    }
}
