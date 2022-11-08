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

import forms.incident.SealIdentificationFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.incident.equipment.seal.SealIdentificationNumberView

class SealIdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String     = "incident.equipment.seal.sealIdentificationNumber"
  val number: String              = Gen.alphaNumStr.sample.value
  override def form: Form[String] = new SealIdentificationFormProvider()(prefix, Nil, number)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[SealIdentificationNumberView].apply(form, mrn, NormalMode, incidentIndex, equipmentIndex, sealIndex, number)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(args = number)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(args = number)

  behave like pageWithoutHint()

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
