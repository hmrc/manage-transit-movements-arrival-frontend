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

package views.incident.equipment.seal

import forms.AddAnotherItemFormProvider
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.incident.AddAnotherSealViewModel
import views.behaviours.ListWithActionsViewBehaviours
import views.html.incident.equipment.seal.AddAnotherSealView

class AddAnotherSealViewSpec extends ListWithActionsViewBehaviours {

  private val mode: Mode = arbitrary[Mode].sample.value

  override def maxNumber: Int = frontendAppConfig.maxSeals

  private def formProvider = new AddAnotherItemFormProvider()

  override val prefix: String = "incident.equipment.seal.addAnotherSeal.withoutContainer"

  private val viewModel = AddAnotherSealViewModel(
    listItems,
    prefix,
    controllers.incident.equipment.seal.routes.AddAnotherSealController.onSubmit(mrn, mode, incidentIndex, equipmentIndex),
    listItems.length
  )

  override def form: Form[Boolean] = formProvider(prefix, viewModel.allowMoreSeals)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherSealView]
      .apply(
        form = form,
        mrn = mrn,
        viewModel = viewModel
      )(fakeRequest, messages, frontendAppConfig)

  override def applyMaxedOutView: HtmlFormat.Appendable =
    injector
      .instanceOf[AddAnotherSealView]
      .apply(
        form = formProvider(prefix, allowMoreItems = false),
        mrn = mrn,
        viewModel = AddAnotherSealViewModel(
          maxedOutListItems,
          prefix,
          controllers.incident.equipment.seal.routes.AddAnotherSealController.onSubmit(mrn, mode, incidentIndex, equipmentIndex),
          maxedOutListItems.length
        )
      )(fakeRequest, messages, frontendAppConfig)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithMoreItemsAllowed()

  behave like pageWithItemsMaxedOut()

  behave like pageWithSubmitButton("Continue")
}
