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
import views.html.DeclarationSubmittedView
import views.behaviours.PanelViewBehaviours

class DeclarationSubmittedViewSpec extends PanelViewBehaviours {

  override val prefix: String = "declarationSubmitted"

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, officeOfDestination)(fakeRequest, messages)

  behave like pageWithTitle()

  behave like pageWithoutBackLink()

  behave like pageWithHeading()

  behave like pageWithPanel(
    body = s"for Movement Reference Number (MRN) $mrn"
  )

  behave like pageWithContent("h2", "What happens next")

  behave like pageWithLink(
    id = "manage-transit-movements",
    expectedText = "Check the status of arrival notifications",
    expectedHref = "http://localhost:9485/manage-transit-movements/view-arrivals"
  )

  behave like pageWithPartialContent(
    "p",
    s"If the goods are not released when expected or you have another problem, contact Customs at Test on +44 7760663422."
  )

  behave like pageWithLink(
    id = "new-arrival",
    expectedText = "Create another arrival notification",
    expectedHref = "movement-reference-number"
  )

  "Customs office with no telephone" - {
    val view = injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, officeOfDestinationNoTel)(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If the goods are not released when expected or you have another problem, contact Customs at Test."
    )

  }

}
