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

import models.journeyDomain.incident.equipment.EquipmentDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.AddTransportEquipmentPage
import pages.incident.equipment.ContainerIdentificationNumberPage
import pages.sections.incident.EquipmentsSection
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs.GET
import utils.AnswersHelper
import viewModels.ListItem

class EquipmentsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(EquipmentsSection(incidentIndex)) {
      position =>
        val equipmentIndex = Index(position)

        val removeRoute: Option[Call] = if (userAnswers.get(AddTransportEquipmentPage(incidentIndex)).isEmpty && position == 0) {
          None
        } else {
          Some(Call(GET, "#")) // TODO - remove incident page
        }

        buildListItemWithDefault[EquipmentDomain, String](
          page = ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
          formatJourneyDomainModel = _.toString,
          formatType = _.fold("")(identity), // TODO - what should be rendered when container id is not present?
          removeRoute = removeRoute
        )(EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex), implicitly)
    }
}

object EquipmentsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index)(implicit messages: Messages) =
    new EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
}
