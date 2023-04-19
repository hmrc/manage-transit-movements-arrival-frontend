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

import forms.SelectableFormProvider
import models.reference.CustomsOffice
import models.{NormalMode, SelectableList}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.identification.DestinationOfficeView

class DestinationOfficeViewSpec extends InputSelectViewBehaviours[CustomsOffice] {

  override def form: Form[CustomsOffice] = new SelectableFormProvider()(prefix, SelectableList(values))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[DestinationOfficeView].apply(form, mrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[CustomsOffice] = arbitraryCustomsOffice

  override val prefix: String = "identification.destinationOffice"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals")

  behave like pageWithHeading()

  behave like pageWithContent("p", "This is the customs office where the CTC transit ends.")

  behave like pageWithSelect()

  behave like pageWithHint("Enter the office location or code, like Wien-Flughafen or AT330200.")

  behave like pageWithSubmitButton("Continue")
}
