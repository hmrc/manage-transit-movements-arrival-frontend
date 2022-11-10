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

package views.incident.equipment.seal

import forms.YesNoFormProvider
import models.NormalMode
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.equipment.seal.ConfirmRemoveSealView

class ConfirmRemoveSealViewSpec extends YesNoViewBehaviours {

  private val identificationNumber: String = Gen.alphaNumStr.sample.value

  override def form: Form[Boolean] = new YesNoFormProvider()(prefix, identificationNumber)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[ConfirmRemoveSealView]
      .apply(form, mrn, NormalMode, incidentIndex, equipmentIndex, sealIndex, identificationNumber)(fakeRequest, messages)

  override val prefix: String = "incident.equipment.seal.remove"

  behave like pageWithTitle(identificationNumber)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(identificationNumber)

  behave like pageWithRadioItems(args = Seq(identificationNumber))

  behave like pageWithSubmitButton("Continue")
}
