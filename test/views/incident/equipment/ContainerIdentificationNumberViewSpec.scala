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

package views.incident.equipment

import forms.incident.ContainerIdentificationFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.incident.equipment.ContainerIdentificationNumberView
import org.scalacheck.{Arbitrary, Gen}

class ContainerIdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "incident.equipment.containerIdentificationNumber"

  override def form: Form[String] = new ContainerIdentificationFormProvider()(prefix, Nil)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[ContainerIdentificationNumberView].apply(form, mrn, NormalMode, incidentIndex, equipmentIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithHint("This can be up to 17 characters long and include both letters and numbers, for example AABB3322110.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
