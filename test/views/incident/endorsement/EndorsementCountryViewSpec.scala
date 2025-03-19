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

package views.incident.endorsement

import forms.SelectableFormProvider.CountryFormProvider
import models.reference.Country
import models.{NormalMode, SelectableList}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.incident.endorsement.EndorsementCountryView

class EndorsementCountryViewSpec extends InputSelectViewBehaviours[Country] {

  private val formProvider = new CountryFormProvider()

  override val field: String = formProvider.field

  override def form: Form[Country] = formProvider.apply(prefix, SelectableList(values))

  override def applyView(form: Form[Country]): HtmlFormat.Appendable =
    injector.instanceOf[EndorsementCountryView].apply(form, mrn, values, NormalMode, index)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[Country] = arbitraryCountry

  override val prefix: String = "incident.endorsement.country"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("Enter the country, like Italy or Spain.")

  behave like pageWithSubmitButton("Continue")
}
