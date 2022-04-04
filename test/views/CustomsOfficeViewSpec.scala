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

package views

import forms.{CustomsOfficeFormProvider, GoodsLocationFormProvider}
import models.reference.CustomsOffice
import models.{GoodsLocation, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.{InputSelectViewBehaviours, RadioViewBehaviours}
import views.html.CustomsOfficeView

class CustomsOfficeViewSpec extends InputSelectViewBehaviours[CustomsOffice] {

  override def form: Form[CustomsOffice] = new CustomsOfficeFormProvider()()

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[CustomsOfficeView].apply(form, Nil, mrn, NormalMode, "")(fakeRequest, messages)

  override val prefix: String = "customsOffice"

  behave like pageWithBackLink

  behave like pageWithHeading

  behave like pageWithSelect()

  behave like pageWithSubmitButton("Continue")
}
