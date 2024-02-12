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

import models.journeyDomain.{JourneyDomainModel, JsArrayGettableAsReaderOps, Read}
import models.{Index, RichJsArray, UserAnswers}
import pages.sections.Section
import pages.sections.incident.SealsSection

case class SealsDomain(
  value: Seq[SealDomain]
)(incidentIndex: Index, equipmentIndex: Index)
    extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[_]] = Some(SealsSection(incidentIndex, equipmentIndex))
}

object SealsDomain {

  implicit def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): Read[SealsDomain] = {

    val sealsReader: Read[Seq[SealDomain]] =
      SealsSection(incidentIndex, equipmentIndex).arrayReader.to {
        case x if x.isEmpty =>
          SealDomain.userAnswersReader(incidentIndex, equipmentIndex, Index(0)).toSeq
        case x =>
          x.traverse[SealDomain](SealDomain.userAnswersReader(incidentIndex, equipmentIndex, _).apply(_))
      }

    sealsReader.map(SealsDomain.apply(_)(incidentIndex, equipmentIndex))
  }
}
