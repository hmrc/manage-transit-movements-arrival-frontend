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

package views.incident

import forms.IncidentCodeFormProvider
import generators.Generators
import views.behaviours.InputSelectViewBehaviours
import models.NormalMode
import models.reference.IncidentCode
import models.IncidentCodeList
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.html.incident.IncidentCodeView

class IncidentCodeViewSpec extends InputSelectViewBehaviours[IncidentCode] with Generators {

  private lazy val incidentCode1 = arbitraryIncidentCode.arbitrary.sample.get
  private lazy val incidentCode2 = arbitraryIncidentCode.arbitrary.sample.get
  private lazy val incidentCode3 = arbitraryIncidentCode.arbitrary.sample.get

  override def values: Seq[IncidentCode] =
    Seq(
      incidentCode1,
      incidentCode2,
      incidentCode3
    )

  override def form: Form[IncidentCode] = new IncidentCodeFormProvider()(prefix, IncidentCodeList(values))

  override def applyView(form: Form[IncidentCode]): HtmlFormat.Appendable =
    injector.instanceOf[IncidentCodeView].apply(form, mrn, values, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "incident.incidentCode"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("Hint")

  behave like pageWithSubmitButton("Continue")
}
