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

package views.events.transhipments

import forms.events.transhipments.AddContainerFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ListWithActionsViewBehaviours
import views.html.events.transhipments.AddContainerView

class AddContainerViewSpec extends ListWithActionsViewBehaviours {

  private def formProvider = new AddContainerFormProvider()

  override def form: Form[Boolean] = formProvider(true)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddContainerView]
      .apply(form, mrn, eventIndex, NormalMode, (_, _) => listItem, allowMoreContainers = _ => true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddContainerView]
      .apply(formProvider(false), mrn, eventIndex, NormalMode, (_, _) => maxedOutListItems, allowMoreContainers = _ => false)(fakeRequest, messages)

  override val prefix: String = "addContainer"

  behave like pageWithBackLink

  behave like pageWithMoreItemsAllowed()

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Continue")
}
