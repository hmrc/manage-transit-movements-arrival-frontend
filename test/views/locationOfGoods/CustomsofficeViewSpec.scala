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

import forms.CustomsOfficeFormProvider
import generators.Generators
import models.{CustomsOfficeList, NormalMode}
import models.reference.CustomsOffice
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours

class CustomsofficeViewSpec extends InputSelectViewBehaviours[CustomsOffice] with Generators {

  private lazy val customsOffice1 = arbitraryCustomsOffice.arbitrary.sample.get
  private lazy val customsOffice2 = arbitraryCustomsOffice.arbitrary.sample.get
  private lazy val customsOffice3 = arbitraryCustomsOffice.arbitrary.sample.get

  override def values: Seq[CustomsOffice] =
    Seq(
      customsOffice1,
      customsOffice2,
      customsOffice3
    )

  override def form: Form[CustomsOffice] = new CustomsOfficeFormProvider()(prefix, CustomsOfficeList(values))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[CustomsofficeView].apply(form, mrn, values, NormalMode)(fakeRequest, messages)

  override val prefix: String = "identification.customsoffice"

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSelect

  behave like pageWithHint("CustomsOffice hint")

  behave like pageWithSubmitButton("Continue")
}
