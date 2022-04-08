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

import forms.events.ConfirmRemoveEventFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.events.ConfirmRemoveEventView

class ConfirmRemoveEventViewSpec extends YesNoViewBehaviours {

  private val eventTitle: String = "eventTitle"

  override def form: Form[Boolean] = new ConfirmRemoveEventFormProvider()(eventTitle)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[ConfirmRemoveEventView].apply(form, mrn, eventIndex, NormalMode, eventTitle)(fakeRequest, messages)

  override val prefix: String = "confirmRemoveEvent"

  behave like pageWithTitle(eventTitle)

  behave like pageWithBackLink

  behave like pageWithHeading(eventTitle)

  behave like pageWithRadioItems(args = Seq(eventTitle))
}
