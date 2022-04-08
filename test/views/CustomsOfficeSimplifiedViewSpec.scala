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

import forms.CustomsOfficeSimplifiedFormProvider
import generators.Generators
import models.reference.CustomsOffice
import models.{CustomsOfficeList, NormalMode}
import org.scalacheck.Gen
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.InputSelectViewBehaviours
import views.html.CustomsOfficeSimplifiedView

class CustomsOfficeSimplifiedViewSpec extends InputSelectViewBehaviours[CustomsOffice] with Generators {

  override def form: Form[CustomsOffice] = new CustomsOfficeSimplifiedFormProvider()(arg, CustomsOfficeList(Nil))

  override def applyView(form: Form[CustomsOffice]): HtmlFormat.Appendable =
    injector.instanceOf[CustomsOfficeSimplifiedView].apply(form, values, mrn, NormalMode, arg)(fakeRequest, messages)

  override val prefix: String = "customsOffice.simplified"

  override def values: Seq[CustomsOffice] = Seq(
    CustomsOffice("first", Some("First"), None),
    CustomsOffice("second", Some("Second"), None),
    CustomsOffice("third", Some("Third"), None)
  )

  private val arg: String = Gen.alphaNumStr.sample.value

  behave like pageWithTitle()

  behave like pageWithBackLink

  behave like pageWithHeading(arg)

  behave like pageWithSelect()

  behave like pageWithHint("Give the office location or code. For example, Dover or GB000060.")

  behave like pageWithSubmitButton("Continue")
}
