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

package views.incident.equipment.seal

import forms.AddAnotherItemFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.incident.AddAnotherSealViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.incident.equipment.seal.AddAnotherSealView

class AddAnotherSealViewSpec extends ListWithActionsViewBehaviours {

  override val hiddenChangeText: String => String = x => s"Change seal $x"
  override val hiddenRemoveText: String => String = x => s"Remove seal $x"

  override def maxNumber: Int = frontendAppConfig.maxSeals

  private def formProvider(viewModel: AddAnotherSealViewModel) =
    new AddAnotherItemFormProvider()(viewModel.prefix, viewModel.allowMoreSeals)

  private val viewModel                     = arbitrary[AddAnotherSealViewModel].sample.value
  private val viewModelWithSealsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithSealsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override val prefix: String = viewModelWithSealsNotMaxedOut.prefix

  override def form: Form[Boolean] = formProvider(viewModelWithSealsNotMaxedOut)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherSealView]
      .apply(
        form = form,
        mrn = mrn,
        viewModel = viewModelWithSealsNotMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherSealView]
      .apply(
        form = formProvider(viewModelWithSealsMaxedOut),
        mrn = mrn,
        viewModel = viewModelWithSealsMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithMoreItemsAllowed(viewModelWithSealsNotMaxedOut.args*)()

  behave like pageWithItemsMaxedOut(viewModelWithSealsMaxedOut.args*)

  behave like pageWithSubmitButton("Continue")
}
