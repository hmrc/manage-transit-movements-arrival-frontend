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

import forms.identification.MovementReferenceNumberFormProvider
import models.MovementReferenceNumber
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.identification.MovementReferenceNumberView

class MovementReferenceNumberViewSpec extends InputTextViewBehaviours[MovementReferenceNumber] {

  override val prefix: String = "movementReferenceNumber"

  override def form: Form[MovementReferenceNumber] = new MovementReferenceNumberFormProvider().apply()

  implicit override val arbitraryT: Arbitrary[MovementReferenceNumber] = arbitraryMovementReferenceNumber

  override def applyView(form: Form[MovementReferenceNumber]): HtmlFormat.Appendable =
    injector.instanceOf[MovementReferenceNumberView].apply(form)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals")

  behave like pageWithHeading()

  behave like pageWithHint(
    "This will be 18 characters long and include both letters and numbers."
  )

  behave like pageWithContent("p", "You can find this in the top-right corner of the Transit Accompanying Document (TAD) that’s with the goods.")

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
