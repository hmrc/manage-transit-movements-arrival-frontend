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

import forms.locationOfGoods.UnLocodeFormProvider
import models.reference.UnLocode
import models.{NormalMode, UnLocodeList}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.locationOfGoods.UnlocodeView

class UnlocodeViewSpec extends InputSelectViewBehaviours[UnLocode] {

  override val prefix: String = "locationOfGoods.unlocode"

  override def form: Form[UnLocode] = new UnLocodeFormProvider()(prefix, UnLocodeList(values))

  override def applyView(form: Form[UnLocode]): HtmlFormat.Appendable =
    injector.instanceOf[UnlocodeView].apply(form, mrn, values, NormalMode)(fakeRequest, messages)

  implicit override val arbitraryT: Arbitrary[UnLocode] = arbitraryUnLocode

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithSectionCaption("Arrivals - Location of goods")

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("Enter the location or code, like Berlin or DEBER.")

  behave like pageWithContent("p", "This is a 5-character code used to identify a transit-related location, like a port or clearance depot.")

  behave like pageWithSubmitButton("Continue")
}
