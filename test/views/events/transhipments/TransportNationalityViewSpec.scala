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

package views.events.transhipments

import forms.events.transhipments.TransportNationalityFormProvider
import generators.Generators
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.events.transhipments.TransportNationalityView

class TransportNationalityViewSpec extends InputSelectViewBehaviours[Country] with Generators {

  override def form: Form[Country] = new TransportNationalityFormProvider()(CountryList(Nil))

  override def applyView(form: Form[Country]): HtmlFormat.Appendable =
    injector.instanceOf[TransportNationalityView].apply(form, values, mrn, eventIndex, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transportNationality"

  override def values: Seq[Country] = Seq(
    Country(CountryCode("UK"), "United Kingdom"),
    Country(CountryCode("US"), "United States"),
    Country(CountryCode("ES"), "Spain")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithoutHint

  behave like pageWithSubmitButton("Continue")
}
