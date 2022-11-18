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

package models.journeyDomain.incident.seal

import models.journeyDomain.{JourneyDomainModel, JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, RichJsArray}
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.sections.incident.SealsSection
import play.api.libs.json.JsArray

case class SealsDomain(seals: Seq[SealDomain]) extends JourneyDomainModel

object SealsDomain {

  implicit def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[SealsDomain] =
    SealsSection(incidentIndex, equipmentIndex).reader.flatMap {
      case x if x.isEmpty =>
        UserAnswersReader.fail[SealsDomain](SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)))
      case x =>
        x.traverse[SealDomain](SealDomain.userAnswersReader(incidentIndex, equipmentIndex, _)).map(SealsDomain.apply)
    }
}
