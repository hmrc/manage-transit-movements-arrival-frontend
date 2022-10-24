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
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.identification.authorisation.AuthorisationType._
import models.{Index, Mode, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.IsSimplifiedProcedurePage
import pages.identification.authorisation._
import viewModels.ListItem

class AuthorisationsAnswersHelperSpec extends SpecBase with Generators with ArrivalUserAnswersGenerator {

  "AuthorisationsAnswersHelper" - {

    "authorisation" - {
      "must return None" - {
        "when authorisation undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = AuthorisationsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.authorisation(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when authorisation defined" in {
          forAll(arbitrary[AuthorisationType], Gen.alphaNumStr, arbitrary[Mode]) {
            (authorisationType, referenceNumber, mode) =>
              val answers = emptyUserAnswers
                .setValue(AuthorisationTypePage(index), authorisationType)
                .setValue(AuthorisationReferenceNumberPage(index), referenceNumber)

              val helper = AuthorisationsAnswersHelper(answers, mode)
              val result = helper.authorisation(index).get

              result.key.value mustBe "Authorisation 1"
              result.value.value mustBe s"$authorisationType - $referenceNumber"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.CheckAuthorisationAnswersController.onPageLoad(answers.mrn, index).url
              action.visuallyHiddenText.get mustBe "authorisation 1"
              action.id mustBe "change-authorisation-1"
          }
        }
      }
    }

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          val userAnswers = emptyUserAnswers

          val helper = AuthorisationsAnswersHelper(userAnswers, NormalMode)
          helper.listItems mustBe Nil
        }
      }

      "when user answers populated with a complete authorisation" - {
        "must return one list item" in {
          val ref = Gen.alphaNumStr.sample.value
          val userAnswers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
            .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.ACE)
            .setValue(AuthorisationReferenceNumberPage(Index(0)), ref)

          val helper = AuthorisationsAnswersHelper(userAnswers, NormalMode)
          helper.listItems mustBe Seq(
            Right(
              ListItem(
                name = s"ACE - $ref",
                changeUrl = routes.CheckAuthorisationAnswersController.onPageLoad(userAnswers.mrn, Index(0)).url,
                removeUrl = Some(routes.ConfirmRemoveAuthorisationController.onPageLoad(userAnswers.mrn, Index(0)).url)
              )
            )
          )
        }
      }
    }
  }

}
