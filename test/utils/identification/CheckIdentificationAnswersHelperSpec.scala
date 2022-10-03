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
import controllers.identification.authorisation.{routes => authRoutes}
import controllers.identification.routes
import generators.Generators
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType.Option1
import models.{Mode, MovementReferenceNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification._
import pages.identification.authorisation._
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

import java.time.LocalDate

class CheckIdentificationAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "CheckIdentificationAnswersHelper" - {

    "movementReferenceNumber" - {
      "must return row" in {
        forAll(arbitrary[Mode], arbitrary[MovementReferenceNumber]) {
          (mode, mrn) =>
            val answers = emptyUserAnswers.copy(mrn = mrn)

            val helper = new CheckIdentificationAnswersHelper(answers, mode)
            val result = helper.movementReferenceNumber

            result mustBe SummaryListRow(
              key = Key("Movement Reference Number (MRN)".toText),
              value = Value(s"$mrn".toText),
              actions = Some(
                Actions(
                  items = List(
                    ActionItem(
                      content = "Change".toText,
                      href = routes.MovementReferenceNumberController.onPageLoad().url,
                      visuallyHiddenText = Some("Movement Reference Number (MRN)"),
                      attributes = Map()
                    )
                  )
                )
              )
            )
        }
      }
    }

    "arrivalDate" - {
      "must return None" - {
        "when ArrivalDatePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new CheckIdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.arrivalDate
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ArrivalDatePage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val date    = LocalDate.of(2000: Int, 1: Int, 1: Int)
              val answers = emptyUserAnswers.setValue(ArrivalDatePage, date)

              val helper = new CheckIdentificationAnswersHelper(answers, mode)
              val result = helper.arrivalDate

              result mustBe Some(
                SummaryListRow(
                  key = Key("Arrival Date".toText),
                  value = Value("1 January 2000".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.ArrivalDateController.onPageLoad(answers.mrn, mode).url,
                          visuallyHiddenText = Some("arrival date"),
                          attributes = Map("id" -> "change-arrival-date")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "procedureType" - {
      "must return None" - {
        "when IsSimplifiedProcedurePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new CheckIdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.isSimplified
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when IsSimplifiedProcedurePage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)

              val helper = new CheckIdentificationAnswersHelper(answers, mode)
              val result = helper.isSimplified

              result mustBe Some(
                SummaryListRow(
                  key = Key("Procedure type".toText),
                  value = Value("Yes".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.IsSimplifiedProcedureController.onPageLoad(answers.mrn, mode).url,
                          visuallyHiddenText = Some("procedure type"),
                          attributes = Map("id" -> "change-is-simplified-procedure")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "identificationNumber" - {
      "must return None" - {
        "when IdentificationNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new CheckIdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.identificationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when IdentificationNumberPage defined" in {
          forAll(arbitrary[Mode], Gen.alphaNumStr) {
            (mode, id) =>
              val answers = emptyUserAnswers.setValue(IdentificationNumberPage, id)

              val helper = new CheckIdentificationAnswersHelper(answers, mode)
              val result = helper.identificationNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Identification Number".toText),
                  value = Value(id.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.IdentificationNumberController.onPageLoad(answers.mrn, mode).url,
                          visuallyHiddenText = Some("identification number"),
                          attributes = Map("id" -> "change-identification-number")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }

    "authorisation" - {
      "must return None" - {
        "when AuthorisationTypePage undefined at index" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new CheckIdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.authorisation(authorisationIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationTypePage defined at index" in {
          forAll(arbitrary[Mode], Gen.alphaNumStr) {
            (mode, ref) =>
              val answers = emptyUserAnswers
                .setValue(AuthorisationTypePage(authorisationIndex), Option1)
                .setValue(AuthorisationReferenceNumberPage(authorisationIndex), ref)

              val helper = new CheckIdentificationAnswersHelper(answers, mode)
              val result = helper.authorisation(authorisationIndex)

              result mustBe Some(
                SummaryListRow(
                  key = Key("Authorisation 1".toText),
                  value = Value("Option 1".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = authRoutes.CheckAuthorisationAnswersController.onPageLoad(answers.mrn, authorisationIndex).url,
                          visuallyHiddenText = Some("authorisation 1"),
                          attributes = Map("id" -> "change-authorisation-1")
                        )
                      )
                    )
                  )
                )
              )
          }
        }
      }
    }
  }
}
