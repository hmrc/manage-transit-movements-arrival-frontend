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

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.routes
import models.requests.{DataRequest, OptionalDataRequest}
import models.{SubmissionStatus, UserAnswers}
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRequiredActionSpec extends SpecBase with EitherValues with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks {

  private object Harness extends DataRequiredActionImpl {
    def callRefine[A](request: OptionalDataRequest[A]): Future[Either[Result, DataRequest[A]]] = refine(request)
  }

  "Data Required Action" - {

    "when there are no UserAnswers" - {

      "must return Left and redirect to session expired" in {

        val harness = Harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, None))

        val result = harness.map(_.left.value)

        status(result) mustBe 303
        redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url
      }
    }

    "when there are UserAnswers" - {

      "and answers have previously been submitted" - {
        "must return Left and redirect to session expired" in {
          val userAnswers = UserAnswers(mrn, eoriNumber, Json.obj(), submissionStatus = SubmissionStatus.Submitted)

          val harness = Harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

          val result = harness.map(_.left.value)

          status(result) mustBe 303
          redirectLocation(result).value mustBe routes.SessionExpiredController.onPageLoad().url
        }
      }

      "and answers have not previously been submitted" - {
        "must return Right with DataRequest" in {
          forAll(Gen.oneOf(SubmissionStatus.NotSubmitted, SubmissionStatus.Amending)) {
            submissionStatus =>
              val userAnswers = UserAnswers(mrn, eoriNumber, Json.obj(), submissionStatus = submissionStatus)

              val result = Harness.callRefine(OptionalDataRequest(fakeRequest, eoriNumber, Some(userAnswers)))

              whenReady[Either[Result, DataRequest[_]], Assertion](result) {
                result =>
                  result.value.userAnswers mustBe userAnswers
                  result.value.eoriNumber mustBe eoriNumber
              }
          }
        }
      }
    }
  }
}
