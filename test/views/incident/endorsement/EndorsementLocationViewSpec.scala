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

import forms.incident.EndorsementLocationFormProvider
import models.NormalMode
import org.scalacheck.{Arbitrary, Gen}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.incident.endorsement.EndorsementLocationView

class EndorsementLocationViewSpec extends InputTextViewBehaviours[String] {

  private lazy val countryName = arbitraryCountry.arbitrary.sample.get.description

  override val prefix: String = "incident.endorsement.location"

  override def form: Form[String] = new EndorsementLocationFormProvider()(prefix, countryName)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[EndorsementLocationView].apply(form, mrn, countryName, NormalMode, index)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(countryName)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(countryName)

  behave like pageWithHint("Describe the specific location of the endorsement. This can be up to 35 characters long.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
