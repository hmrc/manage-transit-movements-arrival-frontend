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

import forms.Constants.maxIncidentTextLength
import forms.incident.IncidentTextFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CharacterCountViewBehaviours
import views.html.incident.IncidentTextView

class IncidentTextViewSpec extends CharacterCountViewBehaviours {

  override val prefix: String = "incident.incidentText"

  override def form: Form[String] = new IncidentTextFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[IncidentTextView].apply(form, mrn, NormalMode, index)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithHint("Describe the incident in detail. This can be up to 512 characters long.")

  behave like pageWithCharacterCount(maxIncidentTextLength)

  behave like pageWithSubmitButton("Continue")
}
