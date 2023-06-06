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

package views.identification.authorisation

import forms.identification.AuthorisationRefNoFormProvider
import models.NormalMode
import models.identification.authorisation.AuthorisationType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.identification.authorisation.AuthorisationReferenceNumberView
import org.scalacheck.{Arbitrary, Gen}

class AuthorisationReferenceNumberViewSpec extends InputTextViewBehaviours[String] {

  override val prefix: String = "identification.authorisation.authorisationReferenceNumber"

  override def form: Form[String] = new AuthorisationRefNoFormProvider()(prefix, AuthorisationType.ACT.toString)

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector
      .instanceOf[AuthorisationReferenceNumberView]
      .apply(form, mrn, AuthorisationType.ACT.toString, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[String] = Arbitrary(Gen.alphaStr)

  behave like pageWithTitle(AuthorisationType.ACT.toString)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Authorisations")

  behave like pageWithHeading(AuthorisationType.ACT.toString)

  behave like pageWithHint("This can be up to 35 characters long and include both letters and numbers.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
