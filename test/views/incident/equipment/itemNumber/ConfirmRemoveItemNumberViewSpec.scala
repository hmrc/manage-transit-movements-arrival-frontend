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

package views.incident.equipment.itemNumber

import forms.YesNoFormProvider
import models.NormalMode
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.equipment.itemNumber.ConfirmRemoveItemNumberView

class ConfirmRemoveItemNumberViewSpec extends YesNoViewBehaviours {

  private val itemNumber: String = Gen.alphaNumStr.sample.value

  override def form: Form[Boolean] = new YesNoFormProvider()(prefix, itemNumber)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[ConfirmRemoveItemNumberView]
      .apply(form, mrn, NormalMode, incidentIndex, equipmentIndex, itemNumberIndex, itemNumber)(fakeRequest, messages)

  override val prefix: String = "incident.equipment.itemNumber.remove"

  behave like pageWithTitle(itemNumber)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(itemNumber)

  behave like pageWithRadioItems(args = Seq(itemNumber))

  behave like pageWithSubmitButton("Continue")
}
