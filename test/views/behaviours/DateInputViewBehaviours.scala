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

package views.behaviours

import java.time.LocalDate

import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.data.FormError

trait DateInputViewBehaviours extends QuestionViewBehaviours[LocalDate] with ScalaCheckPropertyChecks {

  // scalastyle:off method.length
  def pageWithDateInput(): Unit =
    "page with date input" - {
      "when rendered" - {

        "must display day" in {
          assertRenderedById(doc, "value.day")
        }

        "must display month" in {
          assertRenderedById(doc, "value.month")
        }

        "must display year" in {
          assertRenderedById(doc, "value.year")
        }
      }

      "when rendered with error" - {
        behave like pageWithErrorSummary()

        "must show an error class on the inputs" in {
          val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month", "year")))))

          val dayInput = docWithError.getElementById("value.day")
          assert(dayInput.hasClass("govuk-input--error"))

          val monthInput = docWithError.getElementById("value.month")
          assert(monthInput.hasClass("govuk-input--error"))

          val yearInput = docWithError.getElementById("value.year")
          assert(yearInput.hasClass("govuk-input--error"))
        }

        "must have correct href on error link" - {
          "when no args" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.day")
          }

          "when error in day input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.day")
          }

          "when error in month input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.month")
          }

          "when error in year input" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.year")
          }

          "when error in day and month inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.day")
          }

          "when error in day and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.day")
          }

          "when error in month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.month")
          }

          "when error in day, month and year inputs" in {
            val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq("day", "month", "year")))))
            val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
            assertElementContainsHref(link, "#value.day")
          }

          "when error has other args" in {
            forAll(arbitrary[String]) {
              arg =>
                val docWithError = parseView(applyView(form.withError(FormError("value", errorMessage, Seq(arg)))))
                val link         = docWithError.select(".govuk-error-summary__list > li > a").first()
                assertElementContainsHref(link, "#value.day")
            }
          }
        }
      }
    }
  // scalastyle:on method.length
}
