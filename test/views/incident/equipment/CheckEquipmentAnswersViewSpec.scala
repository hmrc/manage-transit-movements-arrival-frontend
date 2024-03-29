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

import controllers.incident.equipment.routes
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import viewModels.sections.Section
import views.behaviours.CheckYourAnswersViewBehaviours
import views.html.incident.equipment.CheckEquipmentAnswersView

class CheckEquipmentAnswersViewSpec extends CheckYourAnswersViewBehaviours {

  private val mode: Mode = arbitrary[Mode].sample.value

  override val prefix: String = "incident.equipment.checkEquipmentAnswers"

  override def view: HtmlFormat.Appendable = viewWithSections(sections)

  override def viewWithSections(sections: Seq[Section]): HtmlFormat.Appendable =
    injector.instanceOf[CheckEquipmentAnswersView].apply(mrn, mode, incidentIndex, equipmentIndex, sections)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Transport equipment")

  behave like pageWithHeading()

  behave like pageWithCheckYourAnswers()

  behave like pageWithFormAction(routes.CheckEquipmentAnswersController.onSubmit(mrn, mode, incidentIndex, equipmentIndex).url)

  behave like pageWithSubmitButton("Continue")

}
