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

package views.incident

import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.AddEndorsementView

class AddEndorsementViewSpec extends YesNoViewBehaviours {

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddEndorsementView].apply(form, mrn, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "incident.addEndorsement"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithoutHint()

  behave like pageWithContent("p", "This is a confirmation that any high-risk or high-value goods remained intact and weren’t stolen during the incident.")

  behave like pageWithContent("p", "An endorsement can only be provided by customs, Border Force or the police.")

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
