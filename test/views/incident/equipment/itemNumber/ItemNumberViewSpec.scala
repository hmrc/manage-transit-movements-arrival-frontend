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

package views.incident.equipment.itemNumber

import controllers.incident.equipment.itemNumber.routes
import forms.ItemNumberFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.incident.equipment.itemNumber.ItemNumberView
import org.scalacheck.{Arbitrary, Gen}

class ItemNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "incident.equipment.itemNumber"

  val call = routes.ItemNumberController.onSubmit(mrn, NormalMode, incidentIndex, equipmentIndex, itemNumberIndex)

  override def form: Form[String] = new ItemNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[ItemNumberView].apply(form, mrn, NormalMode, incidentIndex, equipmentIndex, itemNumberIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithHint("This can be up to 4 numbers long, for example 45 or 1234.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
