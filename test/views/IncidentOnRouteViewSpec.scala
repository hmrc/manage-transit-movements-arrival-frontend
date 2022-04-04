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

package views

import forms.IncidentOnRouteFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.IncidentOnRouteView

class IncidentOnRouteViewSpec extends YesNoViewBehaviours {

  override def form: Form[Boolean] = new IncidentOnRouteFormProvider()()

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[IncidentOnRouteView].apply(form, mrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "incidentOnRoute"

  behave like pageWithBackLink

  behave like pageWithHeading

  behave like pageWithContent("p", "Events should be recorded on the transit accompanying document (TAD) in box 55.")

  behave like pageWithContent("p", "Tell us if:")

  behave like pageWithList(
    listClass = "govuk-list--bullet",
    expectedListItems = "there was an accident",
    "official customs seals were damaged",
    "goods had to be unloaded",
    "goods moved to a different vehicle",
    "goods moved to a different type of transport",
    "the planned route changed"
  )
}
