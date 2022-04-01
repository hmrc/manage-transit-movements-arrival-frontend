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

import forms.GoodsLocationFormProvider
import models.{GoodsLocation, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import views.behaviours.QuestionViewBehaviours
import views.html.GoodsLocationView

class GoodsLocationViewSpec extends QuestionViewBehaviours[GoodsLocation] {

  val radioOptions: Seq[RadioItem]       = Nil //GoodsLocation.radios(form)
  override def form: Form[GoodsLocation] = new GoodsLocationFormProvider()()

  override def applyView(form: Form[GoodsLocation]): HtmlFormat.Appendable =
    injector.instanceOf[GoodsLocationView].apply(form, radioOptions, mrn, NormalMode)(fakeRequest, messages)

  override val prefix: String = "goodsLocation"

  behave like pageWithBackLink

  behave like pageWithHeading

  //behave like pageWithOptions(form, applyView, GoodsLocation.radios(form))

  behave like pageWithSubmitButton("Continue")
}
