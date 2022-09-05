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

package views.locationOfGoods

import forms.locationOfGoods.QualifierofidentificationFormProvider
import models.NormalMode
import models.locationOfGoods.QualifierOfIdentification
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.locationOfGoods.QualifierofidentificationView

class QualifierofidentificationViewSpec extends RadioViewBehaviours[QualifierOfIdentification] {

  override def form: Form[QualifierOfIdentification] = new QualifierofidentificationFormProvider()()

  override def applyView(form: Form[QualifierOfIdentification]): HtmlFormat.Appendable =
    injector.instanceOf[QualifierofidentificationView].apply(form, mrn, QualifierOfIdentification.radioItems, NormalMode)(fakeRequest, messages)

  override val prefix: String = "locationOfGoods.qualifierofidentification"

  override def radioItems(fieldId: String, checkedValue: Option[QualifierOfIdentification] = None): Seq[RadioItem] =
    QualifierOfIdentification.radioItems(fieldId, checkedValue)

  override def values: Seq[QualifierOfIdentification] = QualifierOfIdentification.values

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithSubmitButton("Continue")
}
