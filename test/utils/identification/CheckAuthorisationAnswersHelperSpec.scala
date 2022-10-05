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
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

class CheckAuthorisationAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "CheckAuthorisationAnswersHelper" - {

    "authorisationType" - {
      "must return None" - {
        "when AuthorisationTypePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new CheckAuthorisationAnswersHelper(emptyUserAnswers, mode, authorisationIndex)
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

              val helper = new CheckAuthorisationAnswersHelper(answers, mode, authorisationIndex)
              val result = helper.authorisationType

              result mustBe Some(
                SummaryListRow(
                  key = Key("Type".toText),
                  value = Value("ACT".toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AuthorisationTypeController.onPageLoad(answers.mrn, authorisationIndex, mode).url,
                          visuallyHiddenText = Some("authorisation type"),
                          attributes = Map("id" -> "change-authorisation-type")
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

    "authorisationReferenceNumber" - {
      "must return None" - {
        "when AuthorisationReferenceNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new CheckAuthorisationAnswersHelper(emptyUserAnswers, mode, authorisationIndex)
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

              val helper = new CheckAuthorisationAnswersHelper(answers, mode, authorisationIndex)
              val result = helper.authorisationReferenceNumber

              result mustBe Some(
                SummaryListRow(
                  key = Key("Reference number".toText),
                  value = Value(ref.toText),
                  actions = Some(
                    Actions(
                      items = List(
                        ActionItem(
                          content = "Change".toText,
                          href = routes.AuthorisationReferenceNumberController.onPageLoad(answers.mrn, authorisationIndex, mode).url,
                          visuallyHiddenText = Some("authorisation reference number"),
                          attributes = Map("id" -> "change-authorisation-reference-number")
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
