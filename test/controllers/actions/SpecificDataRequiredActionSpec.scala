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
import models.requests.{DataRequest, SpecificDataRequest}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.http.Status.SEE_OTHER
import play.api.libs.json.JsPath
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import queries.Gettable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SpecificDataRequiredActionSpec extends SpecBase with ScalaCheckPropertyChecks {

  class Harness(page: Gettable[String]) extends SpecificDataRequiredAction(page) {
    def callRefine[A](request: DataRequest[A]): Future[Either[Result, SpecificDataRequest[A]]] = refine(request)
  }

  private case object FakePage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ "foo"
  }

  private def request(userAnswers: UserAnswers): DataRequest[AnyContentAsEmpty.type] = DataRequest(
    fakeRequest,
    eoriNumber,
    userAnswers
  )

  "Specific Data Required Action" - {

    "when required data not present in user answers" - {
      "must redirect to session expired" in {

        val action = new Harness(FakePage)

        val futureResult = action.callRefine(request(emptyUserAnswers))

        whenReady(futureResult) {
          r =>
            val result = Future.successful(r.left.get)
            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }

    "when required data present in user answers" - {
      "must add value to request" in {

        val action = new Harness(FakePage)

        forAll(arbitrary[String]) {
          str =>
            val userAnswers = emptyUserAnswers.setValue(FakePage, str)

            val futureResult = action.callRefine(request(userAnswers))

            whenReady(futureResult) {
              _.right.get.arg mustBe str
            }
        }
      }
    }
  }
}
