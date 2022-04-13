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

package utils

import controllers.events.{routes => eventRoutes}
import models.{Index, Mode, UserAnswers}
import pages.events._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddEventsHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends SummaryListRowHelper(userAnswers) {

  def eventListItem(eventIndex: Index): Option[ListItem] =
    placeOfEvent(eventIndex).map {
      answer =>
        ListItem(
          name = answer,
          changeUrl = eventRoutes.CheckEventAnswersController.onPageLoad(mrn, eventIndex).url,
          removeUrl = eventRoutes.ConfirmRemoveEventController.onPageLoad(mrn, eventIndex, mode).url
        )
    }

  def eventSummaryListRow(eventIndex: Index): Option[SummaryListRow] =
    placeOfEvent(eventIndex) map {
      answer =>
        buildSimpleRow(
          prefix = "addEvent",
          label = messages("addEvent.event.label", eventIndex.display).toText,
          answer = answer.toText,
          id = None,
          call = eventRoutes.CheckEventAnswersController.onPageLoad(mrn, eventIndex),
          args = eventIndex.display,
          answer
        )
    }

  private def placeOfEvent(eventIndex: Index): Option[String] =
    userAnswers.get(EventPlacePage(eventIndex)) orElse
      userAnswers.get(EventCountryPage(eventIndex)).map(_.code)
}
