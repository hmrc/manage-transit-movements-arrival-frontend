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

package views.incident

import forms.AddAnotherItemFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.incident.AddAnotherIncidentViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.incident.AddAnotherIncidentView

class AddAnotherIncidentViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxIncidents

  private def formProvider(viewModel: AddAnotherIncidentViewModel) =
    new AddAnotherItemFormProvider()(viewModel.prefix, viewModel.allowMoreIncidents)

  private val viewModel                         = arbitrary[AddAnotherIncidentViewModel].sample.value
  private val viewModelWithIncidentsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithIncidentsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override val prefix: String = viewModelWithIncidentsNotMaxedOut.prefix

  override def form: Form[Boolean] = formProvider(viewModelWithIncidentsNotMaxedOut)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherIncidentView]
      .apply(
        form = form,
        mrn = mrn,
        viewModel = viewModelWithIncidentsNotMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherIncidentView]
      .apply(
        form = formProvider(viewModelWithIncidentsMaxedOut),
        mrn = mrn,
        viewModel = viewModelWithIncidentsMaxedOut
      )(fakeRequest, messages, frontendAppConfig)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithMoreItemsAllowed(viewModelWithIncidentsNotMaxedOut.numberOfIncidents)()

  behave like pageWithItemsMaxedOut(viewModelWithIncidentsMaxedOut.numberOfIncidents)

  behave like pageWithSubmitButton("Continue")
}
