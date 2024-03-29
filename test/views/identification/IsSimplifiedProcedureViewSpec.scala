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

package views.identification

import forms.EnumerableFormProvider
import models.NormalMode
import models.identification.ProcedureType
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.identification.IsSimplifiedProcedureView

class IsSimplifiedProcedureViewSpec extends RadioViewBehaviours[ProcedureType] {

  override val getValue: ProcedureType => String = _.code

  override def form: Form[ProcedureType] = new EnumerableFormProvider()(prefix)

  override def applyView(form: Form[ProcedureType]): HtmlFormat.Appendable =
    injector.instanceOf[IsSimplifiedProcedureView].apply(form, mrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "identification.isSimplifiedProcedure"

  override def radioItems(fieldId: String, checkedValue: Option[ProcedureType] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[ProcedureType] = ProcedureType.values

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
