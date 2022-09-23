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
import models.UserAnswers
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, UserAnswersReader}
import models.reference.Country
import pages.incident.{IncidentCountryPage, IncidentFlagPage}
import play.api.mvc.Call

// TODO could probably remove the incident flag later as part of the larger domain model
case class IncidentDomain(incidentCountry: Option[Country]) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers): Option[Call] =
    Some(???) // TODO link to next journey
}

object IncidentDomain {

  implicit val userAnswersReader: UserAnswersReader[IncidentDomain] =
    IncidentFlagPage
      .filterOptionalDependent(identity)(IncidentCountryPage.reader)
      .map(IncidentDomain.apply(_))
}
