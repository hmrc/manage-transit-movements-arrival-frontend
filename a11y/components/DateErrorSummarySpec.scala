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

package components

import a11ySpecBase.A11ySpecBase
import forms.DateFormProvider
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.data.FormError
import views.html.components.DateErrorSummary
import views.html.templates.MainTemplate

import java.time.LocalDate

class DateErrorSummarySpec extends A11ySpecBase {

  "the 'date error summary' component" must {
    val template  = app.injector.instanceOf[MainTemplate]
    val component = app.injector.instanceOf[DateErrorSummary]

    val prefix    = Gen.alphaNumStr.sample.value
    val minDate   = arbitrary[LocalDate].sample.value
    val maxDate   = arbitrary[LocalDate].sample.value
    val title     = nonEmptyString.sample.value
    val formError = arbitrary[FormError].sample.value
    val form      = new DateFormProvider()(prefix, minDate, maxDate).withError(formError)

    val content = template.apply(title) {
      component.apply(form).withHeading(title)
    }

    "pass accessibility checks" in {
      content.toString() must passAccessibilityChecks
    }
  }
}
