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

package views.incident.equipment

import models.NormalMode
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.equipment.AddSealsYesNoView

class AddSealsYesNoViewSpec extends YesNoViewBehaviours {

  val number: String = Gen.alphaNumStr.sample.value

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[AddSealsYesNoView].apply(form, number, mrn, NormalMode, incidentIndex, equipmentIndex)(fakeRequest, messages)

  override val prefix: String = "incident.equipment.addSealsYesNo"

  behave like pageWithTitle(args = number)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(args = number)

  behave like pageWithRadioItems(args = Seq(number))

  behave like pageWithSubmitButton("Continue")
}
