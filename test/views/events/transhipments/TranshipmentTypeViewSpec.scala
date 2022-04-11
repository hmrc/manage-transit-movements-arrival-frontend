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

package views.events.transhipments

import forms.events.transhipments.TranshipmentTypeFormProvider
import models.{NormalMode, TranshipmentType}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.events.transhipments.TranshipmentTypeView

class TranshipmentTypeViewSpec extends RadioViewBehaviours[TranshipmentType] {

  override def form: Form[TranshipmentType] = new TranshipmentTypeFormProvider()()

  override def applyView(form: Form[TranshipmentType]): HtmlFormat.Appendable =
    injector.instanceOf[TranshipmentTypeView].apply(form, radioItems, mrn, eventIndex, NormalMode)(fakeRequest, messages)

  override val prefix: String = "transhipmentType"

  override def radioItems(fieldId: String, checkedValue: Option[TranshipmentType] = None): Seq[RadioItem] =
    TranshipmentType.radioItems(fieldId, checkedValue)

  override def values: Seq[TranshipmentType] = TranshipmentType.values

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
