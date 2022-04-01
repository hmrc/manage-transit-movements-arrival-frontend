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

package views

import forms.MovementReferenceNumberFormProvider
import models.MovementReferenceNumber
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewModels.InputSize
import views.behaviours.InputTextViewBehaviours
import views.html.MovementReferenceNumberView

class MovementReferenceNumberViewSpec extends InputTextViewBehaviours[MovementReferenceNumber] {

  override def form: Form[MovementReferenceNumber] = new MovementReferenceNumberFormProvider()()

  override def applyView(form: Form[MovementReferenceNumber]): HtmlFormat.Appendable =
    injector.instanceOf[MovementReferenceNumberView].apply(form)(fakeRequest, messages)

  override val prefix: String = "movementReferenceNumber"

  implicit override val arbitraryT: Arbitrary[MovementReferenceNumber] = arbitraryMovementReferenceNumber

  behave like pageWithBackLink

  behave like pageWithHeading

  behave like pageWithHint(
    "It is on the top right hand corner of the Transit Accompanying Document (TAD) that is with the goods. It is 18 characters, like 19GB12345678901234."
  )

  behave like pageWithInputText(Some(InputSize.Width20))

  behave like pageWithSubmitButton("Continue")
}
