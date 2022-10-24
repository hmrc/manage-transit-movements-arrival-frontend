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
import models.journeyDomain.{JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, RichJsArray}
import pages.sections.incident.IncidentsSection

case class IncidentDomainList(incidentsDomain: Seq[IncidentDomain])

object IncidentDomainList {

  implicit val userAnswersReader: UserAnswersReader[IncidentDomainList] = {

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

    UserAnswersReader[Seq[IncidentDomain]](incidentsReader).map(IncidentDomainList(_))
  }

}
