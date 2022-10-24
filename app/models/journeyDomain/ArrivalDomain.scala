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
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.incident.IncidentDomainList
import models.journeyDomain.locationOfGoods.LocationOfGoodsDomain
import models.{Mode, UserAnswers}
import pages.incident.IncidentFlagPage
import play.api.mvc.Call

case class ArrivalDomain(identification: IdentificationDomain, locationOfGoods: LocationOfGoodsDomain, incidents: Option[IncidentDomainList])
    extends JourneyDomainModel {

  //TODO: Add confirmation page
  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(controllers.routes.CheckTransitionArrivalsAnswersController.onPageLoad(userAnswers.mrn))
}

object ArrivalDomain {

  implicit val userAnswersReader: UserAnswersReader[ArrivalDomain] =
    for {
      identification  <- UserAnswersReader[IdentificationDomain]
      locationOfGoods <- UserAnswersReader[LocationOfGoodsDomain]
      incidents       <- IncidentFlagPage.filterOptionalDependent(identity)(UserAnswersReader[IncidentDomainList])
    } yield ArrivalDomain(identification, locationOfGoods, incidents)
}
