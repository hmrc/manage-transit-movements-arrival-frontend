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

import models.reference.CustomsOffice
import org.scalacheck.Gen
import play.twirl.api.HtmlFormat
import views.html.DeclarationSubmittedView
import views.behaviours.PanelViewBehaviours

class DeclarationSubmittedViewSpec extends PanelViewBehaviours {

  override val prefix: String = "declarationSubmitted"

  val officeOfDestination: CustomsOffice            = new CustomsOffice("ABC12345", Some("Test"), Some("+44 7760663422"))
  val officeOfDestinationNoTel: CustomsOffice       = new CustomsOffice("ABC12345", Some("Test"), None)
  val officeOfDestinationNoNameTel: CustomsOffice   = new CustomsOffice("ABC12345", None, Some("+44 7760663422"))
  val officeOfDestinationNoNameNoTel: CustomsOffice = new CustomsOffice("ABC12345", None, None)

  val oneOfOffices = Gen
    .oneOf(
      officeOfDestination,
      officeOfDestinationNoTel,
      officeOfDestinationNoNameTel,
      officeOfDestinationNoNameNoTel
    )
    .sample
    .value

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, oneOfOffices)(fakeRequest, messages)

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

  behave like pageWithLink(
    id = "new-arrival",
    expectedText = "Create another arrival notification",
    expectedHref = "/manage-transit-movements/arrivals"
  )

  "Customs office with name and telephone" - {
    val view = injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, officeOfDestination)(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If the goods are not released when expected or you have another problem, contact Customs at Test on +44 7760663422."
    )

  }

  "Customs office with name and no telephone" - {
    val view = injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, officeOfDestinationNoTel)(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If the goods are not released when expected or you have another problem, contact Customs at Test."
    )

  }

  "Customs office with no name and a telephone" - {
    val view = injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, officeOfDestinationNoNameTel)(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If the goods are not released when expected or you have another problem, contact Customs office ABC12345 on +44 7760663422."
    )

  }

  "Customs office with no name and no telephone" - {
    val view = injector.instanceOf[DeclarationSubmittedView].apply(mrn.toString, officeOfDestinationNoNameNoTel)(fakeRequest, messages)

    val doc = parseView(view)

    behave like pageWithContent(
      doc,
      "p",
      s"If the goods are not released when expected or you have another problem, contact Customs office ABC12345."
    )

  }

}
