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
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import pages.incident.ContainerIndicatorYesNoPage
import pages.incident.equipment._

case class EquipmentDomain(
  containerId: Option[String]
)(incidentIndex: Index, equipmentIndex: Index)
    extends JourneyDomainModel

object EquipmentDomain {

  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[EquipmentDomain] =
    ContainerIndicatorYesNoPage(incidentIndex).reader.flatMap {
      case true if equipmentIndex.position == 0 =>
        ContainerIdentificationNumberPage(incidentIndex, equipmentIndex).reader.map(
          x => EquipmentDomain(Some(x))(incidentIndex, equipmentIndex)
        )
      case true => ??? // TODO - update when seals section built
      case false =>
        ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
          .filterOptionalDependent(identity)(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex).reader)
          .map(EquipmentDomain(_)(incidentIndex, equipmentIndex))
    }
}
