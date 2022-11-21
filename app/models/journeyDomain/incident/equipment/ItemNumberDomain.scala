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

import models.Index
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import pages.incident.equipment.itemNumber.ItemNumberPage

case class ItemNumberDomain(
  value: String
)(incidentIndex: Index, equipmentIndex: Index, sealIndex: Index)
    extends JourneyDomainModel

object ItemNumberDomain {

  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): UserAnswersReader[ItemNumberDomain] =
    ItemNumberPage(incidentIndex, equipmentIndex, sealIndex).reader.map(ItemNumberDomain(_)(incidentIndex, equipmentIndex, sealIndex))

}