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
import models.requests.DataRequest
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.libs.json.JsPath
import play.api.mvc.AnyContent
import play.api.mvc.Results._
import play.api.test.Helpers._

import scala.concurrent.Future

class ActionsSpec extends SpecBase with ScalaCheckPropertyChecks {

  private case class FakePage(child: String) extends QuestionPage[String] {
    override def path: JsPath = JsPath \ child
  }

  "getPage" - {
    "when value found" - {
      "must return value" in {
        forAll(arbitrary[String], arbitrary[String]) {
          (str, path) =>
            val page        = FakePage(path)
            val userAnswers = emptyUserAnswers.setValue(page, str)

            implicit val request: DataRequest[AnyContent] = DataRequest(fakeRequest, eoriNumber, userAnswers)

            val result = Actions.getPage(page) {
              value =>
                value mustBe str
                Ok
            }

            status(Future.successful(result)) mustBe OK
        }
      }
    }

    "when value not found" - {
      "must redirect to session expired" in {
        forAll(arbitrary[String]) {
          path =>
            implicit val request: DataRequest[AnyContent] = DataRequest(fakeRequest, eoriNumber, emptyUserAnswers)

            val result = Actions.getPage(FakePage(path)) {
              _ => Ok
            }

            redirectLocation(Future.successful(result)).value mustBe
              controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }
  }

  "getPageF" - {
    "when value found" - {
      "must return value" in {
        forAll(arbitrary[String], arbitrary[String]) {
          (str, path) =>
            val page        = FakePage(path)
            val userAnswers = emptyUserAnswers.setValue(page, str)

            implicit val request: DataRequest[AnyContent] = DataRequest(fakeRequest, eoriNumber, userAnswers)

            val result = Actions.getPageF(page) {
              value =>
                value mustBe str
                Future.successful(Ok)
            }

            status(result) mustBe OK
        }
      }
    }

    "when value not found" - {
      "must redirect to session expired" in {
        forAll(arbitrary[String]) {
          path =>
            implicit val request: DataRequest[AnyContent] = DataRequest(fakeRequest, eoriNumber, emptyUserAnswers)

            val result = Actions.getPageF(FakePage(path)) {
              _ => Future.successful(Ok)
            }

            redirectLocation(result).value mustBe
              controllers.routes.SessionExpiredController.onPageLoad().url
        }
      }
    }
  }

}
