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

package views.incident.equipment

import forms.YesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.equipment.ConfirmRemoveEquipmentView

class ConfirmRemoveEquipmentViewSpec extends YesNoViewBehaviours {

  override def form: Form[Boolean] = new YesNoFormProvider()(prefix, equipmentIndex.display)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector
      .instanceOf[ConfirmRemoveEquipmentView]
      .apply(form, mrn, NormalMode, incidentIndex, equipmentIndex)(fakeRequest, messages)

  override val prefix: String = "incident.equipment.remove"

  behave like pageWithTitle(equipmentIndex.display)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(equipmentIndex.display)

  behave like pageWithRadioItems(args = Seq(equipmentIndex.display))

  behave like pageWithSubmitButton("Continue")
}
