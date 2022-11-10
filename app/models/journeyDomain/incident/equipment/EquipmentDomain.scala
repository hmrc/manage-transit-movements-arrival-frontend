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

import cats.implicits._
import models.Index
import models.incident.IncidentCode.SealsBrokenOrTampered
import models.journeyDomain.incident.seal.SealsDomain
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import pages.incident.equipment._
import pages.incident.{ContainerIndicatorYesNoPage, IncidentCodePage}

case class EquipmentDomain(
  containerId: Option[String],
  seals: SealsDomain
)(incidentIndex: Index, equipmentIndex: Index)
    extends JourneyDomainModel

object EquipmentDomain {

  // scalastyle:off cyclomatic.complexity
  def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[EquipmentDomain] = {
    lazy val sealsReads = UserAnswersReader[SealsDomain](SealsDomain.userAnswersReader(incidentIndex, equipmentIndex))

    lazy val optionalSealsReads = AddSealsYesNoPage(incidentIndex, equipmentIndex).reader.flatMap {
      case true  => sealsReads
      case false => UserAnswersReader.apply(SealsDomain(Nil))
    }

    lazy val sealsReadsByIncidentCode = IncidentCodePage(incidentIndex).reader.flatMap {
      case SealsBrokenOrTampered => sealsReads
      case _                     => optionalSealsReads
    }

    ContainerIndicatorYesNoPage(incidentIndex).reader.flatMap {
      case true =>
        (
          ContainerIdentificationNumberPage(incidentIndex, equipmentIndex).reader.map(Some(_)),
          sealsReadsByIncidentCode
        ).mapN {
          (containerId, seals) => EquipmentDomain(containerId, seals)(incidentIndex, equipmentIndex)
        }
      case false if equipmentIndex.position == 0 =>
        ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex).reader.flatMap {
          case true =>
            (
              ContainerIdentificationNumberPage(incidentIndex, equipmentIndex).reader.map(Some(_)),
              sealsReads
            ).mapN {
              (containerId, seals) => EquipmentDomain(containerId, seals)(incidentIndex, equipmentIndex)
            }
          case false =>
            sealsReads.map(EquipmentDomain(None, _)(incidentIndex, equipmentIndex))
        }
      case false =>
        sealsReadsByIncidentCode.map(EquipmentDomain(None, _)(incidentIndex, equipmentIndex))
    }
  }
  // scalastyle:on cyclomatic.complexity
}
