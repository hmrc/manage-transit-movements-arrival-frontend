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

package controllers.actions

import base.SpecBase
import connectors.CacheConnector.APIVersionHeaderMismatchException
import generators.Generators
import models.requests.{IdentifierRequest, OptionalDataRequest}
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{Assertion, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.mvc.Result
import play.api.test.Helpers.*
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends SpecBase with GuiceOneAppPerSuite with ScalaFutures with MockitoSugar with Generators with OptionValues {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private class Harness(sessionRepository: SessionRepository) extends DataRetrievalAction(mrn, sessionRepository) {
    def callRefine[A](request: IdentifierRequest[A]): Future[Either[Result, OptionalDataRequest[A]]] = refine(request)
  }

  "a data retrieval action" - {

    "must return an OptionalDataRequest with an empty UserAnswers" - {

      "where there are no existing answers for this MRN" in {

        when(mockSessionRepository.get(any())(any()))
          .thenReturn(Future.successful(None))

        val harness = new Harness(mockSessionRepository)

        val result = harness.callRefine(IdentifierRequest(fakeRequest, eoriNumber))

        whenReady[Either[Result, OptionalDataRequest[?]], Assertion](result) {
          result =>
            result.value.userAnswers must not be defined
        }
      }
    }

    "must return an OptionalDataRequest with some defined UserAnswers" - {

      "when there are existing answers for this MRN" in {

        when(mockSessionRepository.get(any())(any()))
          .thenReturn(Future.successful(Some(emptyUserAnswers)))

        val harness = new Harness(mockSessionRepository)

        val result = harness.callRefine(IdentifierRequest(fakeRequest, eoriNumber))

        whenReady[Either[Result, OptionalDataRequest[?]], Assertion](result) {
          result =>
            result.value.userAnswers mustBe defined
        }
      }
    }

    "must redirect to 'This arrival notification is no longer available'" - {

      "when call returns a 400" in {

        when(mockSessionRepository.get(any())(any()))
          .thenReturn(Future.failed(new APIVersionHeaderMismatchException(mrn.value)))

        val harness = new Harness(mockSessionRepository)

        val result = harness.callRefine(IdentifierRequest(fakeRequest, eoriNumber)).map(_.left.value)

        status(result) mustBe 303
        redirectLocation(result).value mustBe controllers.routes.DraftNoLongerAvailableController.onPageLoad().url
      }
    }
  }
}
