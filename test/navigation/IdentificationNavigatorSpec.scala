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
import generators.{Generators, IdentificationUserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class IdentificationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with IdentificationUserAnswersGenerator {

  "Identification Navigator" - {

    "in NormalMode" - {

      val navigator = new IdentificationNavigator(NormalMode)

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryIdentificationAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(routes.CheckIdentificationAnswersController.onPageLoad(answers.mrn))
          }
        }
      }
    }

    "in CheckMode" - {

      val navigator = new IdentificationNavigator(CheckMode)

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryIdentificationAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(routes.CheckIdentificationAnswersController.onPageLoad(answers.mrn))
          }
        }
      }
    }
  }
}
