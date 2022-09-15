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

package views.locationOfGoods

import forms.NameFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.TelephoneNumberViewBehaviours
import views.html.locationOfGoods.ContactPersonTelephoneView

class ContactPersonTelephoneViewSpec extends TelephoneNumberViewBehaviours {

  override val prefix: String = "locationOfGoods.contactPersonTelephone"

  private val name: String = Gen.alphaNumStr.sample.value

  override def form: Form[String] = new NameFormProvider()(prefix)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[ContactPersonTelephoneView].apply(form, mrn, name, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading(name)

  behave like pageWithHint("This has to include the country code, like +44 808 157 0192.")

  behave like pageWithTelephoneNumberInput()

  behave like pageWithSubmitButton("Continue")
}
