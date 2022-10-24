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

package models.journeyDomain

import cats.implicits._
import config.FrontendAppConfig
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.incident.IncidentDomainList
import models.journeyDomain.locationOfGoods.LocationOfGoodsDomain
import models.{Mode, UserAnswers}
import pages.incident.IncidentFlagPage
import play.api.mvc.Call

sealed trait ArrivalDomain extends JourneyDomainModel {
  val identification: IdentificationDomain
  val locationOfGoods: LocationOfGoodsDomain

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage)(implicit config: FrontendAppConfig): Option[Call] =
    Some(controllers.routes.CheckArrivalsAnswersController.onPageLoad(userAnswers.mrn))
}

object ArrivalDomain {

  implicit def userAnswersReader(implicit config: FrontendAppConfig): UserAnswersReader[ArrivalDomain] =
    if (config.isPostTransitionEnabled) {
      UserAnswersReader[ArrivalPostTransitionDomain].widen[ArrivalDomain]
    } else {
      UserAnswersReader[ArrivalTransitionDomain].widen[ArrivalDomain]
    }
}

case class ArrivalPostTransitionDomain(
  identification: IdentificationDomain,
  locationOfGoods: LocationOfGoodsDomain,
  incidents: Option[IncidentDomainList]
) extends ArrivalDomain

object ArrivalPostTransitionDomain {

  implicit val userAnswersReaderArrivalPostTransitionDomain: UserAnswersReader[ArrivalPostTransitionDomain] = {
    for {
      identification  <- UserAnswersReader[IdentificationDomain]
      locationOfGoods <- UserAnswersReader[LocationOfGoodsDomain]
      incidents       <- IncidentFlagPage.filterOptionalDependent(identity)(UserAnswersReader[IncidentDomainList])
    } yield ArrivalPostTransitionDomain(identification, locationOfGoods, incidents)
  }
}

case class ArrivalTransitionDomain(
  identification: IdentificationDomain,
  locationOfGoods: LocationOfGoodsDomain
) extends ArrivalDomain

object ArrivalTransitionDomain {

  implicit val userAnswersReaderArrivalPostTransitionDomain: UserAnswersReader[ArrivalTransitionDomain] = {
    for {
      identification  <- UserAnswersReader[IdentificationDomain]
      locationOfGoods <- UserAnswersReader[LocationOfGoodsDomain]
    } yield ArrivalTransitionDomain(identification, locationOfGoods)
  }
}
