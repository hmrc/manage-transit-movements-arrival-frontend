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

import generators.Generators
import models.ArrivalId
import org.scalacheck.Arbitrary.arbitrary
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import views.behaviours.SummaryListViewBehaviours
import views.html.MovementReferenceNumberRejectionView

class MovementReferenceNumberRejectionViewSpec extends SummaryListViewBehaviours with Generators {

  private val arrivalId: ArrivalId = ArrivalId(1)

  private val errorKey = arbitrary[String].sample.value

  override def summaryLists: Seq[SummaryList] = Seq(
    SummaryList(
      rows = Seq(
        SummaryListRow(
          key = Key(content = Text("Movement reference number")),
          value = Value(content = Text(mrn.toString)),
          actions = Some(
            Actions(
              items = Seq(
                ActionItem(
                  href = controllers.routes.UpdateRejectedMRNController.onPageLoad(arrivalId).url,
                  content = Text("Change"),
                  visuallyHiddenText = Some("movement reference number")
                )
              )
            )
          )
        )
      )
    )
  )

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[MovementReferenceNumberRejectionView].apply(arrivalId, errorKey, mrn.toString)(fakeRequest, messages)

  override val prefix: String = "arrivalRejection"

  behave like pageWithTitle()

  behave like pageWithBackLink()

  behave like pageWithHeading()

  pageWithContent("p", messages(errorKey))

  behave like pageWithSummaryLists()

  behave like pageWithPartialContent("p", "If this information is correct, you will need to ")
  behave like pageWithLink(
    id = "contact",
    expectedText = "contact the New Computerised Transit System helpdesk if you need to speak to someone about transit movements (opens in a new tab)",
    expectedHref = frontendAppConfig.nctsEnquiriesUrl
  )
}
