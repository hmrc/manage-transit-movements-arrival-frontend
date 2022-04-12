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
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryList
import viewModels.sections.Section
import views.behaviours.SummaryListViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends SummaryListViewBehaviours {

  override val prefix: String = "checkYourAnswers"

  val sections: Seq[Section] = Seq(
    Section(
      "Section title",
      Nil
    )
  )

  override def summaryLists: Seq[SummaryList] = sections.map(
    section => new SummaryList(section.rows)
  )

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[CheckYourAnswersView].apply(mrn, sections)(fakeRequest, messages)

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithSummaryLists()

  behave like pageWithContent("h2", "Section title")

  behave like pageWithContent("h2", "Now send your arrival notification")

  behave like pageWithContent("p", "By sending this you are confirming that the details you are providing are correct, to the best of your knowledge.")

  behave like pageWithSubmitButton("Confirm and send")

}
