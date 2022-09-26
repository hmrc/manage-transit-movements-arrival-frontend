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

package views.incident

import forms.DateFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateInputViewBehaviours
import views.html.incident.EndorsementDateView
import java.time.{Clock, LocalDate, ZoneOffset}

class EndorsementDateViewSpec extends DateInputViewBehaviours {

  private val minDate = LocalDate.of(2020: Int, 12: Int, 31: Int) //"31 December 2020"
  private val zone    = ZoneOffset.UTC
  private val clock   = Clock.systemDefaultZone.withZone(zone)

  override def form: Form[LocalDate] = new DateFormProvider(clock)(prefix, minDate)

  override def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    injector.instanceOf[EndorsementDateView].apply(form, mrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "incident.endorsementDate"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithDateInput

  behave like pageWithSubmitButton("Continue")
}
