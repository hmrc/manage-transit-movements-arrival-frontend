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
import config.FrontendAppConfig
import models.incident.IncidentCode
import models.journeyDomain.incident.endorsement.EndorsementDomain
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, JourneyDomainModel, Stage, UserAnswersReader}
import models.reference.Country
import models.{Index, Mode, UserAnswers}
import pages.incident.{AddEndorsementPage, IncidentCodePage, IncidentCountryPage, IncidentTextPage}
import play.api.mvc.Call

case class IncidentDomain(
  incidentCountry: Country,
  incidentCode: IncidentCode,
  incidentText: String,
  endorsement: Option[EndorsementDomain]
) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage)(implicit config: FrontendAppConfig): Option[Call] =
    Some(???) // TODO link to next journey
}

object IncidentDomain {

  def userAnswersReader(index: Index): UserAnswersReader[IncidentDomain] =
    (
      IncidentCountryPage(index).reader,
      IncidentCodePage(index).reader,
      IncidentTextPage(index).reader,
      AddEndorsementPage(index).filterOptionalDependent(identity)(UserAnswersReader[EndorsementDomain](EndorsementDomain.userAnswersReader(index)))
    ).mapN {
      (country, code, text, endorsement) => IncidentDomain(country, code, text, endorsement)
    }

}
