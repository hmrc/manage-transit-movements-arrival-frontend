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

package views.incident.equipment.itemNumber

import forms.AddAnotherItemFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.incident.AddAnotherItemNumberViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.incident.equipment.itemNumber.AddAnotherItemNumberYesNoView

class AddAnotherItemNumberYesNoViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxNumberOfItems

  private def formProvider(viewModel: AddAnotherItemNumberViewModel) =
    new AddAnotherItemFormProvider()(viewModel.prefix, viewModel.allowMoreItems)

  private val viewModel                     = arbitrary[AddAnotherItemNumberViewModel].sample.value
  private val viewModelWithItemsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithItemsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override val prefix: String = viewModelWithItemsNotMaxedOut.prefix

  override def form: Form[Boolean] = formProvider(viewModelWithItemsNotMaxedOut)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherItemNumberYesNoView]
      .apply(form = form, mrn = mrn, viewModel = viewModelWithItemsNotMaxedOut)(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherItemNumberYesNoView]
      .apply(
        form = formProvider(viewModelWithItemsMaxedOut),
        mrn = mrn,
        viewModel = viewModelWithItemsMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithMoreItemsAllowed()()

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Continue")
}
