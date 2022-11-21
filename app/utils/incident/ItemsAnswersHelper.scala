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

package utils.incident

import controllers.incident.equipment.itemNumber.routes
import models.journeyDomain.incident.itemNumber.ItemNumberDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.equipment.itemNumber.ItemNumberPage
import pages.sections.incident.ItemsSection
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.AnswersHelper
import viewModels.ListItem

class ItemsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index,
  equipmentIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(ItemsSection(incidentIndex, equipmentIndex)) {
      position =>
        val itemIndex = Index(position)
        val removeRoute: Option[Call] =
          Some(routes.ConfirmRemoveItemNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, itemIndex))

        buildListItem[ItemNumberDomain, String](
          page = ItemNumberPage(incidentIndex, equipmentIndex, Index(position)),
          formatJourneyDomainModel = _.itemNumber,
          formatType = identity,
          removeRoute = removeRoute
        )(ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, itemIndex), implicitly)
    }
}

object ItemsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index, equipmentIndex: Index)(implicit messages: Messages) =
    new ItemsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
}
