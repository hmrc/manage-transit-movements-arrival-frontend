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

import base.SpecBase
import controllers.events.routes._
import models.reference.CountryCode
import models.{CheckMode, Mode}
import pages.events.{EventCountryPage, EventPlacePage}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.govukfrontend.views.Aliases.{ActionItem, Actions, Key, SummaryListRow, Value}
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._

class AddEventsHelperSpec extends SpecBase {

  val mode: Mode = CheckMode

  "AddEventsHelper" - {

    ".eventListItem" - {

      "must return None" - {
        "when EventPlacePage and EventCountryPage undefined" in {

          val helper = new AddEventsHelper(emptyUserAnswers, mode)
          helper.eventListItem(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when EventPlacePage defined" in {

          val place = "PLACE"

          val answers = emptyUserAnswers
            .setValue(EventPlacePage(eventIndex), place)

          val helper = new AddEventsHelper(answers, mode)
          helper.eventListItem(eventIndex) mustBe Some(
            ListItem(
              name = place,
              changeUrl = CheckEventAnswersController.onPageLoad(mrn, eventIndex).url,
              removeUrl = ConfirmRemoveEventController.onPageLoad(mrn, eventIndex, mode).url
            )
          )
        }

        "when EventCountryPage defined" in {

          val countryCode = CountryCode("CODE")

          val answers = emptyUserAnswers
            .set(EventCountryPage(eventIndex), countryCode)
            .success
            .value

          val helper = new AddEventsHelper(answers, mode)
          helper.eventListItem(eventIndex) mustBe Some(
            ListItem(
              name = countryCode.code,
              changeUrl = CheckEventAnswersController.onPageLoad(mrn, eventIndex).url,
              removeUrl = ConfirmRemoveEventController.onPageLoad(mrn, eventIndex, mode).url
            )
          )
        }
      }
    }

    ".cyaListOfEvent" - {

      "must return None" - {
        "when EventPlacePage and EventCountryPage undefined" in {

          val helper = new AddEventsHelper(emptyUserAnswers, mode)
          helper.cyaListOfEvent(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when EventPlacePage defined" in {

          val place = "PLACE"

          val answers = emptyUserAnswers
            .set(EventPlacePage(eventIndex), place)
            .success
            .value

          val helper = new AddEventsHelper(answers, mode)
          helper.cyaListOfEvent(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("addEvent.event.label", eventIndex.display).toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(place.toText),
              actions = Some(
                Actions(items =
                  Seq(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = CheckEventAnswersController.onPageLoad(mrn, eventIndex).url,
                      visuallyHiddenText = Some(messages("addEvent.change.hidden", eventIndex.display, place))
                    )
                  )
                )
              )
            )
          )
        }

        "when EventCountryPage defined" in {

          val countryCode = CountryCode("CODE")

          val answers = emptyUserAnswers
            .set(EventCountryPage(eventIndex), countryCode)
            .success
            .value

          val helper = new AddEventsHelper(answers, mode)
          helper.cyaListOfEvent(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = messages("addEvent.event.label", eventIndex.display).toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(countryCode.code.toText),
              actions = Some(
                Actions(items =
                  Seq(
                    ActionItem(
                      content = messages("site.edit").toText,
                      href = CheckEventAnswersController.onPageLoad(mrn, eventIndex).url,
                      visuallyHiddenText = Some(messages("addEvent.change.hidden", eventIndex.display, countryCode.code))
                    )
                  )
                )
              )
            )
          )
        }
      }
    }
  }

}
