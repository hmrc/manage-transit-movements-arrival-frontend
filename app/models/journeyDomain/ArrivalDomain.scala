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

package models.journeyDomain

import config.PhaseConfig
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.incident.IncidentsDomain
import models.journeyDomain.locationOfGoods.LocationOfGoodsDomain
import models.{Mode, Phase, UserAnswers}
import pages.incident.IncidentFlagPage
import play.api.mvc.Call

sealed trait ArrivalDomain extends JourneyDomainModel {
  val identification: IdentificationDomain
  val locationOfGoods: LocationOfGoodsDomain

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.routes.CheckArrivalsAnswersController.onPageLoad(userAnswers.mrn))
}

object ArrivalDomain {

  implicit def userAnswersReader(implicit phaseConfig: PhaseConfig): Read[ArrivalDomain] = phaseConfig.phase match {
    case Phase.Transition     => ArrivalTransitionDomain.userAnswersReader
    case Phase.PostTransition => ArrivalPostTransitionDomain.userAnswersReader
  }

}

case class ArrivalPostTransitionDomain(
  identification: IdentificationDomain,
  locationOfGoods: LocationOfGoodsDomain
) extends ArrivalDomain

object ArrivalPostTransitionDomain {

  implicit val userAnswersReader: Read[ArrivalDomain] =
    (
      IdentificationDomain.userAnswersReader,
      LocationOfGoodsDomain.userAnswersReader
    ).map(ArrivalPostTransitionDomain.apply)
}

case class ArrivalTransitionDomain(
  identification: IdentificationDomain,
  locationOfGoods: LocationOfGoodsDomain,
  incidents: Option[IncidentsDomain]
) extends ArrivalDomain

object ArrivalTransitionDomain {

  implicit val userAnswersReader: Read[ArrivalDomain] =
    (
      IdentificationDomain.userAnswersReader,
      LocationOfGoodsDomain.userAnswersReader,
      IncidentFlagPage.filterOptionalDependent(identity)(IncidentsDomain.userAnswersReader)
    ).map(ArrivalTransitionDomain.apply)
}
