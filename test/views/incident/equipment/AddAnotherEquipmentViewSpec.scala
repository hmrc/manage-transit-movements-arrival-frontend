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

package views.incident.equipment

import forms.AddAnotherItemFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.incident.AddAnotherEquipmentViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.incident.equipment.AddAnotherEquipmentView

class AddAnotherEquipmentViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxTransportEquipments

  private def formProvider(viewModel: AddAnotherEquipmentViewModel) =
    new AddAnotherItemFormProvider()(viewModel.prefix, viewModel.allowMoreEquipments)

  private val viewModel                     = arbitrary[AddAnotherEquipmentViewModel].sample.value
  private val viewModelWithItemsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithItemsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override val prefix: String = viewModelWithItemsNotMaxedOut.prefix

  override def form: Form[Boolean] = formProvider(viewModelWithItemsNotMaxedOut)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherEquipmentView]
      .apply(
        form = form,
        mrn = mrn,
        viewModel = viewModelWithItemsNotMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherEquipmentView]
      .apply(
        form = formProvider(viewModelWithItemsMaxedOut),
        mrn = mrn,
        viewModel = viewModelWithItemsMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithMoreItemsAllowed(viewModelWithItemsNotMaxedOut.numberOfTransportEquipments)()

  behave like pageWithItemsMaxedOut(viewModelWithItemsMaxedOut.numberOfTransportEquipments)

  behave like pageWithSubmitButton("Continue")
}
