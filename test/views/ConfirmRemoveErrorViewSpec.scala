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
import views.html.ConcurrentRemoveErrorView

class ConcurrentRemoveErrorViewSpec extends ViewBehaviours {

  private val linkText     = "multipleAuthorisation"
  private val redirectLink = "redirectUrl"
  private val journey      = "concurrent.authorisation"
  private val journeyText  = "authorisation"

  override def view: HtmlFormat.Appendable =
    injector.instanceOf[ConcurrentRemoveErrorView].apply(mrn, linkText, redirectLink, journey)(fakeRequest, messages)

  override val prefix: String = "concurrent.remove.error"

  override val urlContainsMrn: Boolean = true

  behave like pageWithTitle(journeyText)

  behave like pageWithBackLink

  behave like pageWithHeading(journeyText)

  behave like pageWithContent("p", "You will need to answer some questions again.")

  behave like pageWithLink(
    "returnLink",
    "Go back to you have added authorisations",
    redirectLink
  )
}
