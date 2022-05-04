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

import _root_.utils.ArrivalRejectionHelper._
import generators.Generators
import models.messages.FunctionalError
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import views.behaviours.SummaryListViewBehaviours
import views.html.ArrivalGeneralRejectionView

class ArrivalGeneralRejectionViewSpec extends SummaryListViewBehaviours with Generators {

  private val functionalErrors: Seq[FunctionalError] = listWithMaxLength[FunctionalError]().sample.value

  override def summaryLists: Seq[SummaryList] = functionalErrors.map(_.toSummaryList)

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[ArrivalGeneralRejectionView].apply(functionalErrors)(fakeRequest, messages)

  override val prefix: String = "arrivalRejection"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  behave like pageWithSummaryLists()

  behave like pageWithPartialContent("p", "You must review the error and ")
  behave like pageWithLink(
    id = "send_new_arrival",
    expectedText = "send a new arrival notification with the right information",
    expectedHref = controllers.routes.MovementReferenceNumberController.onPageLoad().url
  )

  behave like pageWithPartialContent("p", "You can ")
  behave like pageWithLink(
    id = "contact",
    expectedText = "contact the New Computerised Transit System helpdesk if you need help understanding the error (opens in a new tab)",
    expectedHref = frontendAppConfig.nctsEnquiriesUrl
  )
}
