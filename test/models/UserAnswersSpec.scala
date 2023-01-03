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

package models

import base.SpecBase
import generators.Generators
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import pages.identification.DestinationOfficePage
import play.api.libs.json.{JsPath, Json}

import scala.util.Try

class UserAnswersSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val testPageAnswer  = "foo"
  private val testPageAnswer2 = "bar"
  private val testPagePath    = "testPath"

  private val testCleanupPagePath   = "testCleanupPagePath"
  private val testCleanupPageAnswer = "testCleanupPageAnswer"

  final case object TestPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ testPagePath

    override def cleanup(value: Option[String], userAnswers: UserAnswers): Try[UserAnswers] =
      value match {
        case Some(_) => userAnswers.remove(TestCleanupPage)
        case _       => super.cleanup(value, userAnswers)
      }
  }

  final case object TestCleanupPage extends QuestionPage[String] {
    override def path: JsPath = JsPath \ testCleanupPagePath
  }

  "UserAnswers" - {

    "set" - {
      "must run cleanup when given a new answer" in {

        val userAnswers = emptyUserAnswers.setValue(TestCleanupPage, testCleanupPageAnswer)
        val result      = userAnswers.setValue(TestPage, testPageAnswer)

        val data =
          Json.obj(
            testPagePath -> testPageAnswer
          )

        result mustBe UserAnswers(mrn, eoriNumber, data, result.lastUpdated, id = emptyUserAnswers.id)
      }

      "must run cleanup when given a different answer" in {

        val result = emptyUserAnswers
          .setValue(TestPage, testPageAnswer)
          .setValue(TestCleanupPage, testCleanupPageAnswer)
          .setValue(TestPage, testPageAnswer2)

        val data =
          Json.obj(
            testPagePath -> testPageAnswer2
          )

        result mustBe UserAnswers(mrn, eoriNumber, data, result.lastUpdated, id = emptyUserAnswers.id)
      }

      "must not run cleanup when given the same answer" in {

        val result = emptyUserAnswers
          .setValue(TestPage, testPageAnswer)
          .setValue(TestCleanupPage, testCleanupPageAnswer)
          .setValue(TestPage, testPageAnswer)

        val data =
          Json.obj(
            testCleanupPagePath -> testCleanupPageAnswer,
            testPagePath        -> testPageAnswer
          )

        result mustBe UserAnswers(mrn, eoriNumber, data, result.lastUpdated, id = emptyUserAnswers.id)
      }
    }

    "purge" - {
      "when destination office exists" - {
        "must keep the destination office and remove everything else" in {
          forAll(arbitraryArrivalAnswers(emptyUserAnswers)) {
            userAnswers =>
              val destinationOffice = userAnswers.getValue(DestinationOfficePage)
              val result            = userAnswers.purge
              result.data mustBe emptyUserAnswers.setValue(DestinationOfficePage, destinationOffice).data
          }
        }
      }

      "when destination office does not exist" - {
        "must return empty json object" in {
          forAll(arbitraryArrivalAnswers(emptyUserAnswers)) {
            userAnswers =>
              val result = userAnswers.removeValue(DestinationOfficePage).purge
              result.data mustBe Json.obj()
          }
        }
      }
    }
  }
}
