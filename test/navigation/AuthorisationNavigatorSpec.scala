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
import controllers.identification.authorisation.{routes => authorisationRoutes}
import controllers.identification.{routes => identificationRoutes}
import generators.{Generators, IdentificationUserAnswersGenerator}
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class AuthorisationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with IdentificationUserAnswersGenerator {

  private val navigator = new AuthorisationNavigator(authorisationIndex)

  "Authorisation Navigator" - {

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryAuthorisationAnswers(emptyUserAnswers, authorisationIndex)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(authorisationRoutes.CheckAuthorisationAnswersController.onPageLoad(answers.mrn, authorisationIndex))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode = CheckMode

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryIdentificationAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers, mode)
                .mustBe(identificationRoutes.CheckIdentificationAnswersController.onPageLoad(answers.mrn))
          }
        }
      }
    }
  }
}