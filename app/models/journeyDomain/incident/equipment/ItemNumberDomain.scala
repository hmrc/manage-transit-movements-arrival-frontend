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

package models.journeyDomain.incident.equipment

import models.journeyDomain.Stage.{AccessingJourney, CompletingJourney}
import controllers.incident.equipment.itemNumber.routes
import models.{Index, Mode, UserAnswers}
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import pages.incident.equipment.itemNumber.ItemNumberPage
import play.api.mvc.Call

case class ItemNumberDomain(
  itemNumber: String
)(incidentIndex: Index, equipmentIndex: Index, itemNumberIndex: Index)
    extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney  => routes.ItemNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex)
      case CompletingJourney => routes.AddAnotherItemNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex)
    }
  }
}

object ItemNumberDomain {

  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index, itemNumberIndex: Index): UserAnswersReader[ItemNumberDomain] =
    ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex).reader.map(ItemNumberDomain(_)(incidentIndex, equipmentIndex, itemNumberIndex))

}
