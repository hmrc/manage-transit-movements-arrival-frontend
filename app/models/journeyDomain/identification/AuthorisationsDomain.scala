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
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, UserAnswers}
import pages.identification.IsSimplifiedProcedurePage
import pages.identification.authorisation.AuthorisationTypePage
import pages.sections.AuthorisationsSection
import play.api.mvc.Call

case class AuthorisationsDomain(
  value: Seq[AuthorisationDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers): Option[Call] =
    Some(controllers.identification.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.mrn))
}

object AuthorisationsDomain {

  implicit val userAnswersReader: UserAnswersReader[AuthorisationsDomain] =
    IsSimplifiedProcedurePage.reader.flatMap {
      case true =>
        AuthorisationsSection.reader
          .map(_.value.toList)
          .flatMap {
            case Nil =>
              UserAnswersReader.fail[AuthorisationsDomain](AuthorisationTypePage(Index(0)))
            case x =>
              x.zipWithIndex
                .traverse[UserAnswersReader, AuthorisationDomain] {
                  case (_, index) => AuthorisationDomain.userAnswersReader(Index(index))
                }
                .map(AuthorisationsDomain.apply)
          }
      case false =>
        UserAnswersReader(AuthorisationsDomain(Nil))
    }

}
