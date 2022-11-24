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
import controllers.incident.routes
import models.journeyDomain.{JourneyDomainModel, JsArrayGettableAsReaderOps, Stage, UserAnswersReader}
import models.{Index, Mode, RichJsArray, UserAnswers}
import pages.sections.incident.IncidentsSection
import play.api.mvc.Call

case class IncidentsDomain(incidentsDomain: Seq[IncidentDomain]) extends JourneyDomainModel {

  override def routeIfCompleted(userAnswers: UserAnswers, mode: Mode, stage: Stage): Option[Call] =
    Some(routes.AddAnotherIncidentController.onPageLoad(userAnswers.mrn, mode))
}

object IncidentsDomain {

  implicit val userAnswersReader: UserAnswersReader[IncidentsDomain] = {

    val incidentsReader: UserAnswersReader[Seq[IncidentDomain]] =
      IncidentsSection.reader.flatMap {
        case x if x.isEmpty =>
          UserAnswersReader[IncidentDomain](
            IncidentDomain.userAnswersReader(Index(0))
          ).map(Seq(_))

        case x =>
          x.traverse[IncidentDomain](
            IncidentDomain.userAnswersReader
          ).map(_.toSeq)
      }

    UserAnswersReader[Seq[IncidentDomain]](incidentsReader).map(IncidentsDomain(_))
  }

}
