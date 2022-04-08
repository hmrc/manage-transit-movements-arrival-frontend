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

package views.events

import forms.events.AddEventFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours._
import views.html.events.AddEventView

class AddEventViewSpec extends ListWithActionsViewBehaviours {

  private def formProvider = new AddEventFormProvider()

  override def form: Form[Boolean] = formProvider(true)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddEventView]
      .apply(form, mrn, NormalMode, _ => listItem, allowMoreEvents = true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddEventView]
      .apply(formProvider(false), mrn, NormalMode, _ => maxedOutListItems, allowMoreEvents = false)(fakeRequest, messages)

  override val prefix: String = "addEvent"

  behave like pageWithBackLink

  behave like pageWithMoreItemsAllowed {
    behave like pageWithContent("p", "It should be recorded on the transit accompanying document (TAD).")

    behave like pageWithContent("p", "Tell us if:")

    behave like pageWithList(
      listClass = "govuk-list--bullet",
      expectedListItems = "there was an accident",
      "goods had to be unloaded",
      "goods moved to a different vehicle",
      "goods moved to a different type of transport",
      "the planned route changed"
    )
  }

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Continue")
}
