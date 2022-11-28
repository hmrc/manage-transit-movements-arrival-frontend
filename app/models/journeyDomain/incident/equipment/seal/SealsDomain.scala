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

package models.journeyDomain.incident.equipment.seal

import models.journeyDomain.{JsArrayGettableAsReaderOps, UserAnswersReader}
import models.{Index, RichJsArray}
import pages.sections.incident.SealsSection

case class SealsDomain(seals: Seq[SealDomain])

object SealsDomain {

  implicit def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): UserAnswersReader[SealsDomain] =
    SealsSection(incidentIndex, equipmentIndex).reader
      .flatMap {
        case x if x.isEmpty =>
          UserAnswersReader(SealDomain.userAnswersReader(incidentIndex, equipmentIndex, Index(0))).map(Seq(_))
        case x =>
          x.traverse[SealDomain](SealDomain.userAnswersReader(incidentIndex, equipmentIndex, _))
      }
      .map(SealsDomain(_))
}
