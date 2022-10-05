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
import models.{Index, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.DateInputViewBehaviours
import views.html.incident.EndorsementDateView

import java.time.{Clock, LocalDate, ZoneOffset}

class EndorsementDateViewSpec extends DateInputViewBehaviours {

  private val minDate = frontendAppConfig.endorsementDateMin
  private val zone    = ZoneOffset.UTC
  private val clock   = Clock.systemDefaultZone.withZone(zone)

  override def form: Form[LocalDate] = new DateFormProvider(clock)(prefix, minDate)

  override def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
    injector.instanceOf[EndorsementDateView].apply(form, mrn, Index(0), NormalMode)(fakeRequest, messages)

  override val prefix: String = "incident.endorsementDate"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithHint("For example, 15 08 2022.")

  behave like pageWithDateInput

  behave like pageWithSubmitButton("Continue")
}
