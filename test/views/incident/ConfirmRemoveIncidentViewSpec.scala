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

package views.incident

import generators.Generators
import models.NormalMode
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.incident.ConfirmRemoveIncidentView

class ConfirmRemoveIncidentViewSpec extends YesNoViewBehaviours with Generators {

  val incidentDescripion: Option[String] = Some(Gen.alphaStr.sample.value)

  override def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
    injector.instanceOf[ConfirmRemoveIncidentView].apply(form, mrn, NormalMode, incidentIndex, incidentDescripion)(fakeRequest, messages)

  override val prefix: String = "incident.remove"

  behave like pageWithTitle(incidentIndex.display)

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading(incidentIndex.display)

  behave like pageWithInsetText(incidentDescripion.value)

  behave like pageWithRadioItems(args = Seq(incidentIndex.display))

  behave like pageWithSubmitButton("Continue")
}
