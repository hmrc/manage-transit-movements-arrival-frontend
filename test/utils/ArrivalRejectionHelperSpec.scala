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

package utils

import base.SpecBase
import generators.Generators
import models.ArrivalId
import models.messages.ErrorType.{DuplicateMrn, InvalidMrn, MRNError, UnknownMrn}
import models.messages.FunctionalError
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import utils.ArrivalRejectionHelper._

class ArrivalRejectionHelperSpec extends SpecBase with Generators {

  private val arrivalId: ArrivalId = ArrivalId(1)

  "ArrivalRejectionHelper" - {

    "RichFunctionalError" - {
      ".toSummaryList" - {
        "must return summary list" in {
          forAll(arbitrary[FunctionalError]) {
            functionalError =>
              functionalError.toSummaryList mustBe SummaryList(
                rows = Seq(
                  SummaryListRow(
                    key = "Error code".toKey,
                    value = Value(functionalError.errorType.toString.toText)
                  ),
                  SummaryListRow(
                    key = "Pointer".toKey,
                    value = Value(functionalError.pointer.value.toText)
                  )
                )
              )
          }
        }
      }
    }

    "RichMRNError" - {
      ".errorMessage" - {
        "when UnknownMrn" in {
          UnknownMrn.errorMessage mustBe "The movement reference number cannot be found."
        }

        "when DuplicateMrn" in {
          DuplicateMrn.errorMessage mustBe "The movement reference number is already in use."
        }

        "when InvalidMrn" in {
          InvalidMrn.errorMessage mustBe "The movement reference number is not real."
        }
      }

      ".toSummaryList" - {
        "must return summary list" in {
          forAll(Gen.alphaNumStr, arbitrary[MRNError]) {
            (mrn, mrnError) =>
              mrnError.toSummaryList(arrivalId, mrn) mustBe SummaryList(
                rows = Seq(
                  SummaryListRow(
                    key = "Movement reference number".toKey,
                    value = Value(mrn.toText),
                    actions = Some(
                      Actions(
                        items = Seq(
                          ActionItem(
                            href = controllers.routes.UpdateRejectedMRNController.onPageLoad(arrivalId).url,
                            content = "Change".toText,
                            visuallyHiddenText = Some("movement reference number")
                          )
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
