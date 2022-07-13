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

package views.identification

import forms.AddItemFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ListWithActionsViewBehaviours
import views.html.identification.AddAnotherAuthorisationView

class AddAnotherAuthorisationViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxIdentificationAuthorisations

  private def formProvider = new AddItemFormProvider()

  override def form: Form[Boolean] = formProvider(prefix, allowMoreItems = true)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherAuthorisationView]
      .apply(form, mrn, listItems, allowMoreItems = true)(fakeRequest, messages)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherAuthorisationView]
      .apply(formProvider(prefix, allowMoreItems = false), mrn, maxedOutListItems, allowMoreItems = false)(fakeRequest, messages)

  override val prefix: String = "identification.addAnotherAuthorisation"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
