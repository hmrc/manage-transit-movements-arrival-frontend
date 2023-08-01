/*
 * Copyright 2023 HM Revenue & Customs
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

import config.PhaseConfig
import controllers.incident.equipment.routes
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.AddTransportEquipmentPage
import pages.incident.equipment.ContainerIdentificationNumberPage
import pages.sections.incident.EquipmentsSection
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.AnswersHelper
import viewModels.ListItem

class EquipmentsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index
)(implicit messages: Messages, phaseConfig: PhaseConfig)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(EquipmentsSection(incidentIndex)) {
      equipmentIndex =>
        val removeRoute: Option[Call] = if (userAnswers.get(AddTransportEquipmentPage(incidentIndex)).isEmpty && equipmentIndex.isFirst) {
          None
        } else {
          Some(routes.ConfirmRemoveEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex))
        }

        buildListItemWithDefault[EquipmentDomain, String](
          page = ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
          formatJourneyDomainModel = _.asString,
          formatType = EquipmentDomain.asString(_, equipmentIndex),
          removeRoute = removeRoute
        )(EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex), implicitly)
    }
}

object EquipmentsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index)(implicit messages: Messages, phaseConfig: PhaseConfig) =
    new EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
}
