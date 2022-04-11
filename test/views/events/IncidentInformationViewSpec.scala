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

package views.events

import forms.IncidentInformationFormProvider
import models.{Index, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.CharacterCountViewBehaviours
import views.html.events.IncidentInformationView

class IncidentInformationViewSpec extends CharacterCountViewBehaviours {

  private val maxCharacterCount   = 350
  override def form: Form[String] = new IncidentInformationFormProvider()()

  override def applyView(form: Form[String]): HtmlFormat.Appendable =
    injector.instanceOf[IncidentInformationView].apply(form, mrn, NormalMode, Index(1))(fakeRequest, messages)

  override val prefix: String = "incidentInformation"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithCharacterCount(maxCharacterCount)

  behave like pageWithHint(s"You can enter up to $maxCharacterCount characters")

  behave like pageWithSubmitButton("Continue")
}
