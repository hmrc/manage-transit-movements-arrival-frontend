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
import viewModels.sections.Section
import views.behaviours.ViewBehaviours
import views.html.CheckYourAnswersView

class CheckYourAnswersViewSpec extends ViewBehaviours {

  override val prefix: String = "checkYourAnswers"

  val answersList: Seq[Section] = Seq(
    Section(
      messages("checkYourAnswers.section.traderDetails"),
      Nil
    )
  )

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[CheckYourAnswersView].apply(mrn, answersList)(fakeRequest, messages)

  behave like pageWithBackLink

  behave like pageWithHeading()

  behave like pageWithContent("h2", messages("checkYourAnswers.confirmation.heading"))

  behave like pageWithContent("h2", messages("checkYourAnswers.section.traderDetails"))

  behave like pageWithContent("p", messages("checkYourAnswers.confirmation.paragraph"))

  behave like pageWithSubmitButton(messages("site.confirmAndSend"))

}
