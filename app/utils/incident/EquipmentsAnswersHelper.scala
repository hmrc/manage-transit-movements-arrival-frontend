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

import controllers.incident.equipment.routes
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.AddTransportEquipmentPage
import pages.incident.equipment.ContainerIdentificationNumberPage
import pages.sections.incident.EquipmentsSection
import play.api.i18n.Messages
import utils.{AnswersHelper, RichListItems}
import viewModels.ListItem

class EquipmentsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(EquipmentsSection(incidentIndex)) {
      equipmentIndex =>
        buildListItemWithDefault[EquipmentDomain, String](
          page = ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
          formatJourneyDomainModel = _.asString,
          formatType = EquipmentDomain.asString(_, equipmentIndex),
          removeRoute = Some(routes.ConfirmRemoveEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex))
        )(EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex), implicitly)
    }.checkRemoveLinks(userAnswers.get(AddTransportEquipmentPage(incidentIndex)).isEmpty)
}

object EquipmentsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index)(implicit messages: Messages) =
    new EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
}
