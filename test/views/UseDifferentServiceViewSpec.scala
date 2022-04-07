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

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.UseDifferentServiceView

class UseDifferentServiceViewSpec extends ViewBehaviours {

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[UseDifferentServiceView].apply()(fakeRequest, messages)

  override val prefix: String = "useDifferentService"

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithPartialContent("p", "Use the ")
  behave like pageWithLink(
    "contact",
    "New Computerised Transit System (NCTS)",
    "https://www.gov.uk/new-computerised-transit-system"
  )

  behave like pageWithPartialContent("p", "if the goods are at an authorised consignee’s location.")

  behave like pageWithContent("p", "We are improving this service. You will be able to do this in future.")
}
