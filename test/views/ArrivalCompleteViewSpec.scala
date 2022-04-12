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

import controllers.routes
import play.twirl.api.HtmlFormat
import views.behaviours.PanelViewBehaviours
import views.html.ArrivalCompleteView

class ArrivalCompleteViewSpec extends PanelViewBehaviours {

  private lazy val makeAnotherUrl = routes.MovementReferenceNumberController.onPageLoad().url

  override val urlContainsMrn: Boolean = true

  override val prefix: String = "arrivalComplete"

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[ArrivalCompleteView].apply(mrn, "test paragraph", "test contact us")(fakeRequest, messages)

  behave like pageWithoutBackLink

  behave like pageWithHeading()

  behave like pageWithPanel(
    body = s"for movement reference number $mrn"
  )

  behave like pageWithContent("h2", "What you need to do next")

  behave like pageWithLink(
    id = "check-the-status-of-arrival-notifications",
    expectedText = "Check the status of arrival notifications",
    expectedHref = "http://localhost:9485/manage-transit-movements/view-arrivals"
  )

  behave like pageWithPartialContent("p", "test paragraph")

  behave like pageWithContent("p", "test contact us")

  behave like pageWithLink(
    id = "make-another",
    expectedText = "Make another arrival notification",
    expectedHref = makeAnotherUrl
  )
}
