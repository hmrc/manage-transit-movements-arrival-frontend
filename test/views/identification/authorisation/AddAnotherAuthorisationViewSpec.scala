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

package views.identification.authorisation

import forms.AddAnotherItemFormProvider
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.identification.AddAnotherAuthorisationViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.identification.authorisation.AddAnotherAuthorisationView

class AddAnotherAuthorisationViewSpec extends ListWithActionsViewBehaviours {

  override def maxNumber: Int = frontendAppConfig.maxIdentificationAuthorisations

  private def formProvider(viewModel: AddAnotherAuthorisationViewModel) =
    new AddAnotherItemFormProvider()(viewModel.prefix, viewModel.allowMoreAuthorisations)

  private val viewModel                         = arbitrary[AddAnotherAuthorisationViewModel].sample.value
  private val viewModelWithIncidentsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithIncidentsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  override val prefix: String = viewModel.prefix

  override def form: Form[Boolean] = formProvider(viewModelWithIncidentsNotMaxedOut)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherAuthorisationView]
      .apply(form, mrn, viewModelWithIncidentsNotMaxedOut)(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherAuthorisationView]
      .apply(formProvider(viewModelWithIncidentsMaxedOut), mrn, viewModelWithIncidentsMaxedOut)(fakeRequest, messages, frontendAppConfig)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Authorisations")

  behave like pageWithMoreItemsAllowed(listItems.length)()

  behave like pageWithItemsMaxedOut(maxedOutListItems.length)

  behave like pageWithSubmitButton("Continue")
}
