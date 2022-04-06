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
import models.UserAnswers
import models.requests.{DataRequest, OptionalDataRequest}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status.SEE_OTHER
import play.api.mvc.{AnyContent, Result}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with ScalaCheckPropertyChecks {

  class Harness extends DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  private def request(userAnswers: Option[UserAnswers]): OptionalDataRequest[AnyContent] =
    OptionalDataRequest(fakeRequest, eoriNumber, userAnswers)

  "Data Required Action" - {

    "when data is a Some" - {
      "must return data request" in {

        val action = new Harness()

        val userAnswers = emptyUserAnswers

        val futureResult = action.callRefine(request(Some(userAnswers)))

        whenReady(futureResult) {
          r =>
            r.right.get.userAnswers mustBe userAnswers
            r.right.get.eoriNumber mustBe eoriNumber
        }
      }
    }

    "when data is a None" - {
      "must redirect to session expired" in {

        val action = new Harness()

        val futureResult = action.callRefine(request(None))

        whenReady(futureResult) {
          r =>
            val result = Future.successful(r.left.get)
            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }
  }
}
