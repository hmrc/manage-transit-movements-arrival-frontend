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

package controllers.actions

import base.SpecBase
import generators.Generators
import models.Index
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.identification.AuthorisationDomain
import models.journeyDomain.{JourneyDomainModel, UserAnswersReader}
import models.requests._
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import pages.sections.{AuthorisationSection, AuthorisationsSection, Section}
import play.api.libs.json.{JsArray, JsObject}
import play.api.mvc.Result
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RemoveInProgressActionSpec extends SpecBase with Generators {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private class Harness[T <: JourneyDomainModel](
    array: Section[JsArray],
    indexedValue: Index => Section[JsObject]
  )(implicit userAnswersReader: Index => UserAnswersReader[T])
      extends RemoveInProgressAction[T](array, indexedValue)(mockSessionRepository) {

    def callRefine[A](
      request: DataRequest[A]
    ): Future[Either[Result, DataRequest[A]]] =
      refine(request)
  }

  "Remove in-progress action" - {

    "when array empty" - {
      "must return original request" in {
        val request = DataRequest(fakeRequest, eoriNumber, emptyUserAnswers)

        val action = new Harness[AuthorisationDomain](AuthorisationsSection, AuthorisationSection)

        val futureResult = action.callRefine(request)

        whenReady(futureResult) {
          r =>
            val result = r.right.get
            result mustBe request
        }
      }
    }

    "when array has only completed items" - {
      "must return original request" in {
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

        val userAnswers = emptyUserAnswers
          .setValue(AuthorisationTypePage(authorisationIndex), arbitrary[AuthorisationType].sample.value)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), Gen.alphaNumStr.sample.value)

        val request = DataRequest(fakeRequest, eoriNumber, userAnswers)

        val action = new Harness[AuthorisationDomain](AuthorisationsSection, AuthorisationSection)

        val futureResult = action.callRefine(request)

        whenReady(futureResult) {
          r =>
            val result = r.right.get
            result mustBe request
            verify(mockSessionRepository).set(eqTo(userAnswers))
        }
      }
    }

    "when array has in progress items" - {
      "must strip these out and update user answers" in {
        when(mockSessionRepository.set(any())).thenReturn(Future.successful(true))

        val userAnswers = emptyUserAnswers
          .setValue(AuthorisationTypePage(Index(0)), arbitrary[AuthorisationType].sample.value)
          .setValue(AuthorisationReferenceNumberPage(Index(0)), Gen.alphaNumStr.sample.value)
          .setValue(AuthorisationTypePage(Index(1)), arbitrary[AuthorisationType].sample.value)
          .setValue(AuthorisationTypePage(Index(2)), arbitrary[AuthorisationType].sample.value)
          .setValue(AuthorisationReferenceNumberPage(Index(2)), Gen.alphaNumStr.sample.value)
          .setValue(AuthorisationTypePage(Index(3)), arbitrary[AuthorisationType].sample.value)
          .setValue(AuthorisationTypePage(Index(4: Int)), arbitrary[AuthorisationType].sample.value)
          .setValue(AuthorisationReferenceNumberPage(Index(4: Int)), Gen.alphaNumStr.sample.value)

        val request = DataRequest(fakeRequest, eoriNumber, userAnswers)

        val action = new Harness[AuthorisationDomain](AuthorisationsSection, AuthorisationSection)

        val futureResult = action.callRefine(request)

        val expectedAnswers = userAnswers
          .removeValue(AuthorisationSection(Index(3)))
          .removeValue(AuthorisationSection(Index(1)))

        whenReady(futureResult) {
          r =>
            val result = r.right.get
            result.userAnswers mustBe expectedAnswers
            verify(mockSessionRepository).set(eqTo(expectedAnswers))
        }
      }
    }
  }
}