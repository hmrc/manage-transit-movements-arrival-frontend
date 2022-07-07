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

package utils.identification

import base.SpecBase
import controllers.identification.authorisation.routes._
import models.{CheckMode, Mode}
import pages.identification.authorisation._
import uk.gov.hmrc.govukfrontend.views.Aliases._
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddAuthorisationsHelperSpec extends SpecBase {

  private val mode: Mode = CheckMode
  private val prefix     = "identification.addAnotherAuthorisation"

  "AddAuthorisationsHelper" - {

    ".authorisationListItem" - {

      "must return None" - {
        "when AuthorisationReferenceNumberPage and AuthorisationTypePage undefined" in {

          val helper = new AddAuthorisationHelper(prefix, emptyUserAnswers, mode)
          helper.authorisationListItem(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationReferenceNumberPage defined" in {

          val authorisationRef = "authRefNo"

          val answers = emptyUserAnswers
            .setValue(AuthorisationReferenceNumberPage(eventIndex), authorisationRef)

          val helper = new AddAuthorisationHelper(prefix, answers, mode)
          helper.authorisationListItem(eventIndex) mustBe Some(
            ListItem(
              name = authorisationRef,
              changeUrl = CheckAuthorisationAnswersController.onPageLoad(mrn, eventIndex).url,
              removeUrl = "removeUrl" //TODO ConfirmRemoveEventController.onPageLoad(mrn, eventIndex, mode).url
            )
          )
        }
      }
    }

    ".event" - {

      "must return None" - {
        "when AuthorisationReferenceNumberPage undefined" in {

          val helper = new AddAuthorisationHelper(prefix, emptyUserAnswers, mode)
          helper.authorisation(eventIndex) mustBe None
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationReferenceNumberPage defined" in {

          val place = "authRefNo"

          val answers = emptyUserAnswers
            .setValue(AuthorisationReferenceNumberPage(eventIndex), place)

          val helper = new AddAuthorisationHelper(prefix, answers, mode)
          helper.authorisation(eventIndex) mustBe Some(
            SummaryListRow(
              key = Key(
                content = s"Authorisation ${eventIndex.display}".toText,
                classes = "govuk-!-width-one-half"
              ),
              value = Value(place.toText),
              actions = Some(
                Actions(items =
                  Seq(
                    ActionItem(
                      content = "Change".toText,
                      href = AuthorisationReferenceNumberController.onPageLoad(mrn, eventIndex, mode).url, //ToDo CYA Page
                      visuallyHiddenText = Some(s"authorisation ${eventIndex.display}")
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
