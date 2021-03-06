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

package navigation

import base.SpecBase
import controllers.identification.routes
import generators.{Generators, UserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.identification.MovementReferenceNumberPage

class IdentificationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator {

  private val navigator = new IdentificationNavigator

  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {

      case object UnknownPage extends Page

      "when in normal mode" - {
        "to start of the departure journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, NormalMode, answers)
                .mustBe(routes.MovementReferenceNumberController.onPageLoad())
          }
        }
      }

      "when in check mode" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, CheckMode, answers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }

    "must go from Local Reference Number page to Office of Departure page" in {
      forAll(arbitrary[UserAnswers], arbitrary[Mode]) {
        (answers, mode) =>
          navigator
            .nextPage(MovementReferenceNumberPage, mode, answers)
            .mustBe(routes.MovementReferenceNumberController.onPageLoad()) //TODO Update
      }
    }

  }
}
