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

package views.incident.location

import forms.EnumerableFormProvider
import models.NormalMode
import models.reference.QualifierOfIdentification
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.incident.location.QualifierOfIdentificationView

class QualifierOfIdentificationViewSpec extends RadioViewBehaviours[QualifierOfIdentification] {

  override val getValue: QualifierOfIdentification => String = _.code

  override def form: Form[QualifierOfIdentification] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[QualifierOfIdentification]): HtmlFormat.Appendable =
    injector.instanceOf[QualifierOfIdentificationView].apply(form, mrn, values, NormalMode, index)(fakeRequest, messages)

  override val prefix: String = "incident.location.qualifierOfIdentification"

  override def radioItems(fieldId: String, checkedValue: Option[QualifierOfIdentification] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[QualifierOfIdentification] = Seq(
    QualifierOfIdentification("U", "UN/LOCODE"),
    QualifierOfIdentification("W", "GPS coordinates"),
    QualifierOfIdentification("Z", "Free text")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Incidents")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
