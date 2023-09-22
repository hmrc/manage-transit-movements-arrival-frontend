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

import forms.EnumerableFormProvider
import models.NormalMode
import models.reference.IncidentCode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.incident.IncidentCodeView

class IncidentCodeViewSpec extends RadioViewBehaviours[IncidentCode] {

  override val getValue: IncidentCode => String = _.code

  override def form: Form[IncidentCode] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[IncidentCode]): HtmlFormat.Appendable =
    injector.instanceOf[IncidentCodeView].apply(form, mrn, values, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "incident.incidentCode"

  override def radioItems(fieldId: String, checkedValue: Option[IncidentCode] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  private val ic1                        = IncidentCode("1", "test1")
  private val ic2                        = IncidentCode("2", "test2")
  private val ics                        = Seq(ic1, ic2)
  override def values: Seq[IncidentCode] = ics

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithoutHint()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
