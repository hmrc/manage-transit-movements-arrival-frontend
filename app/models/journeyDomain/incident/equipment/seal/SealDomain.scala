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

package models.journeyDomain.incident.equipment.seal

import controllers.incident.equipment.seal.routes
import models.journeyDomain.Stage._
import models.journeyDomain._
import models.{Index, Mode, UserAnswers}
import pages.incident.equipment.seal.SealIdentificationNumberPage
import play.api.mvc.Call

case class SealDomain(
  identificationNumber: String
)(incidentIndex: Index, equipmentIndex: Index, sealIndex: Index)
    extends JourneyDomainModel {

  override def toString: String = identificationNumber

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] = Some {
    stage match {
      case AccessingJourney  => routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, sealIndex)
      case CompletingJourney => routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex)
    }
  }
}

object SealDomain {

  implicit def userAnswersReader(incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): Read[SealDomain] =
    SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex).reader.map(SealDomain(_)(incidentIndex, equipmentIndex, sealIndex))
}
