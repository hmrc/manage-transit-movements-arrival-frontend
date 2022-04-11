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

package views.events.seals

import forms.events.seals.AddSealFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours._
import views.html.events.seals.AddSealView

class AddSealViewSpec extends ListWithActionsViewBehaviours {

  private def formProvider = new AddSealFormProvider()

  override def form: Form[Boolean] = formProvider(true)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddSealView]
      .apply(form, mrn, eventIndex, NormalMode, (_, _) => listItem, allowMoreSeals = _ => true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddSealView]
      .apply(formProvider(false), mrn, eventIndex, NormalMode, (_, _) => maxedOutListItems, allowMoreSeals = _ => false)(fakeRequest, messages)

  override val prefix: String = "addSeal"

  behave like pageWithBackLink

  behave like pageWithMoreItemsAllowed()

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Continue")
}
