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

package models.journeyDomain.incident

import cats.implicits._
import models.Index
import models.incident.IncidentCode
import models.journeyDomain.incident.endorsement.EndorsementDomain
import models.journeyDomain.incident.equipment.EquipmentsDomain
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import pages.incident.{AddEndorsementPage, IncidentCodePage, IncidentCountryPage, IncidentTextPage}

case class IncidentDomain(
  incidentCountry: Country,
  incidentCode: IncidentCode,
  incidentText: String,
  endorsement: Option[EndorsementDomain],
  location: IncidentLocationDomain,
  equipments: EquipmentsDomain
)(index: Index)
    extends JourneyDomainModel

object IncidentDomain {

  def userAnswersReader(index: Index): UserAnswersReader[IncidentDomain] =
    (
      IncidentCountryPage(index).reader,
      IncidentCodePage(index).reader,
      IncidentTextPage(index).reader,
      AddEndorsementPage(index).filterOptionalDependent(identity)(UserAnswersReader[EndorsementDomain](EndorsementDomain.userAnswersReader(index))),
      UserAnswersReader[IncidentLocationDomain](IncidentLocationDomain.userAnswersReader(index)),
      UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(index))
    ).tupled.map((IncidentDomain.apply _).tupled).map(_(index))

}
