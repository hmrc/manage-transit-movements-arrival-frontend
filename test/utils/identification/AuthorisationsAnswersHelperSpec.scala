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

package utils.identification

import base.SpecBase
import controllers.identification.authorisation.routes
import controllers.locationOfGoods.routes.TypeOfLocationController
import generators.Generators
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.identification.authorisation.AuthorisationType._
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.Assertion
import pages.identification.IsSimplifiedProcedurePage
import pages.identification.authorisation._
import pages.sections.identification.AuthorisationSection
import play.api.libs.json.Json
import viewModels.ListItem

class AuthorisationsAnswersHelperSpec extends SpecBase with Generators {

  "AuthorisationsAnswersHelper" - {

    "authorisations" - {
      "must return no rows" - {
        "when no authorisations defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = AuthorisationsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.authorisations
              result mustBe Nil
          }
        }
      }

      "must return rows" - {
        "when authorisations defined" in {
          forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxIdentificationAuthorisations)) {
            (mode, count) =>
              val userAnswersGen = (0 until count).foldLeft(Gen.const(emptyUserAnswers)) {
                (acc, i) =>
                  acc.flatMap(arbitraryAuthorisationAnswers(_, Index(i)))
              }
              forAll(userAnswersGen) {
                userAnswers =>
                  val helper = AuthorisationsAnswersHelper(userAnswers, mode)
                  val result = helper.authorisations
                  result.size mustBe count
              }
          }
        }
      }
    }

    "addOrRemoveAuthorisations" - {
      "must return None" - {
        "when authorisations array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = AuthorisationsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addOrRemoveAuthorisation
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when authorisations array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AuthorisationSection(Index(0)), Json.obj("foo" -> "bar"))
              val helper  = AuthorisationsAnswersHelper(answers, mode)
              val result  = helper.addOrRemoveAuthorisation.get

              result.id mustBe "add-or-remove-authorisations"
              result.text mustBe "Add or remove authorisations"
              result.href mustBe routes.AddAnotherAuthorisationController.onPageLoad(answers.mrn, mode).url
          }
        }
      }
    }

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
        "when authorisation defined" - {

          def test(authorisationType: AuthorisationType, label: String): Assertion =
            forAll(Gen.alphaNumStr, arbitrary[Mode]) {
              (referenceNumber, mode) =>
                val answers = emptyUserAnswers
                  .setValue(AuthorisationTypePage(index), authorisationType)
                  .setValue(AuthorisationReferenceNumberPage(index), referenceNumber)

                val helper = AuthorisationsAnswersHelper(answers, mode)
                val result = helper.authorisation(index).get

                result.key.value mustBe "Authorisation 1"
                result.value.value mustBe s"$label - $referenceNumber"
                val actions = result.actions.get.items
                actions.size mustBe 1
                val action = actions.head
                action.content.value mustBe "Change"
                action.href mustBe TypeOfLocationController.onPageLoad(answers.mrn, mode).url
                action.visuallyHiddenText.get mustBe "authorisation 1"
                action.id mustBe "change-authorisation-1"
            }

          "when ACT" in {
            test(AuthorisationType.ACT, "ACT")
          }

          "when ACE" in {
            test(AuthorisationType.ACE, "ACE")
          }
        }
      }
    }

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = AuthorisationsAnswersHelper(userAnswers, mode)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with a complete authorisation" - {
        "must return one list item" - {

          def test(authorisationType: AuthorisationType, label: String): Assertion =
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, ref) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
                  .setValue(AuthorisationTypePage(Index(0)), authorisationType)
                  .setValue(AuthorisationReferenceNumberPage(Index(0)), ref)

                val helper = AuthorisationsAnswersHelper(userAnswers, mode)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = s"$label - $ref",
                      changeUrl = TypeOfLocationController.onPageLoad(userAnswers.mrn, mode).url,
                      removeUrl = Some(routes.ConfirmRemoveAuthorisationController.onPageLoad(userAnswers.mrn, Index(0), mode).url)
                    )
                  )
                )
            }

          "when ACT" in {
            test(AuthorisationType.ACT, "ACT")
          }

          "when ACE" in {
            test(AuthorisationType.ACE, "ACE")
          }
        }
      }

      "when user answers populated with an in-progress authorisation" - {
        "must return one list item" - {

          def test(authorisationType: AuthorisationType, label: String): Assertion =
            forAll(arbitrary[Mode]) {
              mode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
                  .setValue(AuthorisationTypePage(Index(0)), authorisationType)

                val helper = AuthorisationsAnswersHelper(userAnswers, mode)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = label,
                      changeUrl = routes.AuthorisationReferenceNumberController.onPageLoad(userAnswers.mrn, Index(0), mode).url,
                      removeUrl = Some(routes.ConfirmRemoveAuthorisationController.onPageLoad(userAnswers.mrn, Index(0), mode).url)
                    )
                  )
                )
            }

          "when ACT" in {
            test(AuthorisationType.ACT, "ACT")
          }

          "when ACE" in {
            test(AuthorisationType.ACE, "ACE")
          }
        }
      }
    }
  }

}
