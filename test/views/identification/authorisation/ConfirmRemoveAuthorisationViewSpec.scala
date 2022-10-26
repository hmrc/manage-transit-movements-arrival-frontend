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

package views.identification.authorisation

import forms.ConfirmRemoveItemFormProvider
import models.NormalMode
import models.identification.authorisation.AuthorisationType
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.identification.authorisation.ConfirmRemoveAuthorisationView

class ConfirmRemoveAuthorisationViewSpec extends YesNoViewBehaviours {

  private val authorisationTitle: String = Gen.alphaNumStr.sample.value
  private val authorisationType          = AuthorisationType.ACT

  override def form: Form[Boolean] = new ConfirmRemoveItemFormProvider()(prefix, authorisationTitle, authorisationType)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[ConfirmRemoveAuthorisationView]
      .apply(form, mrn, authorisationIndex, NormalMode, authorisationTitle, authorisationType.toString)(fakeRequest, messages)

  override val prefix: String = "identification.authorisation.confirmRemoveAuthorisation"

  behave like pageWithTitle(authorisationType.toString, authorisationTitle)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Authorisations")

  behave like pageWithHeading(authorisationType.toString, authorisationTitle)

  behave like pageWithRadioItems(args = Seq(authorisationType.toString, authorisationTitle))

  behave like pageWithSubmitButton("Continue")
}
