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

package views.locationOfGoods

import forms.EnumerableFormProvider
import models.NormalMode
import models.reference.TypeOfLocation
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.RadioViewBehaviours
import views.html.locationOfGoods.TypeOfLocationView

class TypeOfLocationViewSpec extends RadioViewBehaviours[TypeOfLocation] {

  override val getValue: TypeOfLocation => String = _.code

  override def form: Form[TypeOfLocation] = new EnumerableFormProvider()(prefix, values)

  override def applyView(form: Form[TypeOfLocation]): HtmlFormat.Appendable =
    injector.instanceOf[TypeOfLocationView].apply(form, mrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "locationOfGoods.typeOfLocation"

  override def radioItems(fieldId: String, checkedValue: Option[TypeOfLocation] = None): Seq[RadioItem] =
    values.toRadioItems(fieldId, checkedValue)

  override def values: Seq[TypeOfLocation] = Seq(
    TypeOfLocation("A", "Designated location"),
    TypeOfLocation("B", "Authorised place"),
    TypeOfLocation("C", "Approved place"),
    TypeOfLocation("D", "Other")
  )

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithSectionCaption("Arrivals - Location of goods")

  behave like pageWithHeading()

  behave like pageWithRadioItems()

  behave like pageWithContent("p", "This is their location at the end of the transit movement.")

  behave like pageWithSubmitButton("Continue")
}
