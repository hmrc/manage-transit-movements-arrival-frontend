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

package utils.identification

import base.SpecBase
import controllers.identification.authorisation.routes
import generators.Generators
import models.Mode
import models.identification.authorisation.AuthorisationType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}

class AuthorisationAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "CheckAuthorisationAnswersHelper" - {

    "authorisationType" - {
      "must return None" - {
        "when AuthorisationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new AuthorisationAnswersHelper(emptyUserAnswers, mode, authorisationIndex)
              val result = helper.authorisationType
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationTypePage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AuthorisationTypePage(authorisationIndex), AuthorisationType.ACT)

              val helper = new AuthorisationAnswersHelper(answers, mode, authorisationIndex)
              val result = helper.authorisationType.get

              result.key.value mustBe "Type"
              result.value.value mustBe "ACT"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AuthorisationTypeController.onPageLoad(answers.mrn, authorisationIndex, mode).url
              action.visuallyHiddenText.get mustBe "authorisation type"
              action.id mustBe "change-authorisation-type"
          }
        }
      }
    }

    "authorisationReferenceNumber" - {
      "must return None" - {
        "when AuthorisationReferenceNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new AuthorisationAnswersHelper(emptyUserAnswers, mode, authorisationIndex)
              val result = helper.authorisationReferenceNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationReferenceNumberPage defined" in {
          forAll(arbitrary[Mode], Gen.alphaNumStr) {
            (mode, ref) =>
              val answers = emptyUserAnswers.setValue(AuthorisationReferenceNumberPage(authorisationIndex), ref)

              val helper = new AuthorisationAnswersHelper(answers, mode, authorisationIndex)
              val result = helper.authorisationReferenceNumber.get

              result.key.value mustBe "Reference number"
              result.value.value mustBe ref
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AuthorisationReferenceNumberController.onPageLoad(answers.mrn, authorisationIndex, mode).url
              action.visuallyHiddenText.get mustBe "authorisation reference number"
              action.id mustBe "change-authorisation-reference-number"
          }
        }
      }
    }
  }
}
