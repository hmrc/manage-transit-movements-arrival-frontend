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
import models.UserAnswers
import models.requests._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.http.Status.SEE_OTHER
import play.api.libs.json.{JsPath, Reads}
import play.api.mvc.{AnyContentAsEmpty, Result}
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import queries.Gettable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SpecificDataRequiredActionSpec extends SpecBase with ScalaCheckPropertyChecks {

  private class Harness1[T1](pages: Gettable[T1]*)(implicit rds: Reads[T1]) extends SpecificDataRequiredAction1[T1](pages*) {

    def callRefine[A](
      request: DataRequest[A]
    ): Future[Either[Result, SpecificDataRequestProvider1[T1]#SpecificDataRequest[A]]] =
      refine(request)
  }

  private class Harness2[T1, T2](page: Gettable[T2])(implicit rds: Reads[T2]) extends SpecificDataRequiredAction2[T1, T2](page) {

    def callRefine[A](
      request: SpecificDataRequestProvider1[T1]#SpecificDataRequest[A]
    ): Future[Either[Result, SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest[A]]] =
      refine(request)
  }

  private class Harness3[T1, T2, T3](page: Gettable[T3])(implicit rds: Reads[T3]) extends SpecificDataRequiredAction3[T1, T2, T3](page) {

    def callRefine[A](
      request: SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest[A]
    ): Future[Either[Result, SpecificDataRequestProvider3[T1, T2, T3]#SpecificDataRequest[A]]] =
      refine(request)
  }

  private case object FooPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ "foo"
  }

  private case object BarPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ "bar"
  }

  "Specific Data Required Action" - {

    "getFirst" - {

      type T = Either[Result, SpecificDataRequestProvider1[String]#SpecificDataRequest[?]]

      def request(userAnswers: UserAnswers): DataRequest[AnyContentAsEmpty.type] =
        DataRequest(fakeRequest, eoriNumber, userAnswers)

      "when checking one page" - {

        "when required data not present in user answers" - {
          "must redirect to session expired" in {

            val action = new Harness1(FooPage)

            val futureResult = action.callRefine(request(emptyUserAnswers))

            whenReady[T, Assertion](futureResult) {
              r =>
                val result = Future.successful(r.left.value)
                status(result) mustEqual SEE_OTHER
                redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
            }
          }
        }

        "when required data present in user answers" - {
          "must add value to request" in {

            val action = new Harness1(FooPage)

            forAll(arbitrary[String]) {
              str =>
                val userAnswers = emptyUserAnswers.setValue(FooPage, str)

                val futureResult = action.callRefine(request(userAnswers))

                whenReady[T, Assertion](futureResult) {
                  _.value.arg mustEqual str
                }
            }
          }
        }
      }

      "when checking multiple pages" - {

        "when all pages is present in user answers" - {
          "must add value to request of the first page" in {

            val action = new Harness1(FooPage, BarPage)

            forAll(arbitrary[String], arbitrary[String]) {
              (foo, bar) =>
                val userAnswers = emptyUserAnswers
                  .setValue(FooPage, foo)
                  .setValue(BarPage, bar)

                val futureResult = action.callRefine(request(userAnswers))

                whenReady[T, Assertion](futureResult) {
                  _.value.arg mustEqual foo
                }
            }
          }
        }

        "when first page not present but second page is present in user answers" - {
          "must add value to request of the second page" in {

            val action = new Harness1(FooPage, BarPage)

            forAll(arbitrary[String]) {
              bar =>
                val userAnswers = emptyUserAnswers.setValue(BarPage, bar)

                val futureResult = action.callRefine(request(userAnswers))

                whenReady[T, Assertion](futureResult) {
                  _.value.arg mustEqual bar
                }
            }
          }
        }

        "when no pages present in user answers" - {
          "must redirect to session expired" in {

            val action = new Harness1(FooPage, BarPage)

            val futureResult = action.callRefine(request(emptyUserAnswers))

            whenReady[T, Assertion](futureResult) {
              r =>
                val result = Future.successful(r.left.value)
                status(result) mustEqual SEE_OTHER
                redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
            }
          }
        }
      }
    }

    "getSecond" - {

      type T = Either[Result, SpecificDataRequestProvider2[String, String]#SpecificDataRequest[?]]

      def request(userAnswers: UserAnswers, arg1: String): SpecificDataRequestProvider1[String]#SpecificDataRequest[AnyContentAsEmpty.type] =
        new SpecificDataRequestProvider1[String].SpecificDataRequest(fakeRequest, eoriNumber, userAnswers, arg1)

      "when required data not present in user answers" - {
        "must redirect to session expired" in {

          val action = new Harness2[String, String](FooPage)

          forAll(arbitrary[String]) {
            str1 =>
              val futureResult = action.callRefine(request(emptyUserAnswers, str1))

              whenReady[T, Assertion](futureResult) {
                r =>
                  val result = Future.successful(r.left.value)
                  status(result) mustEqual SEE_OTHER
                  redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
              }
          }
        }
      }

      "when required data present in user answers" - {
        "must add value to request" in {

          val action = new Harness2[String, String](FooPage)

          forAll(arbitrary[String], arbitrary[String]) {
            (str1, str2) =>
              val userAnswers = emptyUserAnswers.setValue(FooPage, str2)

              val futureResult = action.callRefine(request(userAnswers, str1))

              whenReady[T, Assertion](futureResult) {
                r =>
                  r.value.arg._1 mustEqual str1
                  r.value.arg._2 mustEqual str2
              }
          }
        }
      }
    }

    "getThird" - {

      type T = Either[Result, SpecificDataRequestProvider3[String, String, String]#SpecificDataRequest[?]]

      def request(
        userAnswers: UserAnswers,
        arg1: String,
        arg2: String
      ): SpecificDataRequestProvider2[String, String]#SpecificDataRequest[AnyContentAsEmpty.type] =
        new SpecificDataRequestProvider2[String, String].SpecificDataRequest(fakeRequest, eoriNumber, userAnswers, (arg1, arg2))

      "when required data not present in user answers" - {
        "must redirect to session expired" in {

          val action = new Harness3[String, String, String](FooPage)

          forAll(arbitrary[String], arbitrary[String]) {
            (str1, str2) =>
              val futureResult = action.callRefine(request(emptyUserAnswers, str1, str2))

              whenReady[T, Assertion](futureResult) {
                r =>
                  val result = Future.successful(r.left.value)
                  status(result) mustEqual SEE_OTHER
                  redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
              }
          }
        }
      }

      "when required data present in user answers" - {
        "must add value to request" in {

          val action = new Harness3[String, String, String](FooPage)

          forAll(arbitrary[String], arbitrary[String], arbitrary[String]) {
            (str1, str2, str3) =>
              val userAnswers = emptyUserAnswers.setValue(FooPage, str3)

              val futureResult = action.callRefine(request(userAnswers, str1, str2))

              whenReady[T, Assertion](futureResult) {
                r =>
                  r.value.arg._1 mustEqual str1
                  r.value.arg._2 mustEqual str2
                  r.value.arg._3 mustEqual str3
              }
          }
        }
      }
    }
  }
}
