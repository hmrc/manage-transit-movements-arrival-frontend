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

package views.incident.transportMeans

import forms.SelectableFormProvider
import models.reference.Nationality
import models.{NormalMode, SelectableList}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.incident.transportMeans.TransportNationalityView

class TransportNationalityViewSpec extends InputSelectViewBehaviours[Nationality] {

  override def form: Form[Nationality] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[Nationality]): HtmlFormat.Appendable =
    injector.instanceOf[TransportNationalityView].apply(form, mrn, values, NormalMode, incidentIndex)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[Nationality] = arbitraryNationality

  override val prefix: String = "incident.transportMeans.transportNationality"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithSelect()

  behave like pageWithHint("Enter the country or code, like Austria or AT.")

  behave like pageWithSubmitButton("Continue")
}
