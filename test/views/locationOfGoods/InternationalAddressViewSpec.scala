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

import forms.InternationalAddressFormProvider
import generators.Generators
import models.{CountryList, InternationalAddress, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InternationalAddressViewBehaviours
import views.html.locationOfGoods.InternationalAddressView

class InternationalAddressViewSpec extends InternationalAddressViewBehaviours with Generators {

  private val countryList = arbitrary[CountryList].sample.value

  override def form: Form[InternationalAddress] = new InternationalAddressFormProvider()(prefix, countryList)

  override def applyView(form: Form[InternationalAddress]): HtmlFormat.Appendable =
    injector.instanceOf[InternationalAddressView].apply(form, mrn, NormalMode, countryList.countries)(fakeRequest, messages)

  override val prefix: String = "locationOfGoods.internationalAddress"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithAddressInput()

  behave like pageWithSubmitButton("Continue")
}