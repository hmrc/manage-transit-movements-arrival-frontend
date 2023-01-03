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

package views.identification

import forms.EoriNumberFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.identification.IdentificationNumberView

class IdentificationNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "identification.identificationNumber"

  override def form: Form[String] = new EoriNumberFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[IdentificationNumberView].apply(form, mrn, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals")

  behave like pageWithHeading()

  behave like pageWithContent("p", "The consignee is the person or company receiving the goods at the office of destination.")

  behave like pageWithHint(
    "This number can be up to 17 characters long and include both letters and numbers. For example, GB123456789000."
  )

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
