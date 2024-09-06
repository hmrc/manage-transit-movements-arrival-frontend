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

package models.journeyDomain.incident.equipment.itemNumber

import models.journeyDomain._
import models.{Index, RichJsArray, UserAnswers}
import pages.sections.Section
import pages.sections.incident.ItemsSection

case class ItemNumbersDomain(
  value: Seq[ItemNumberDomain]
)(incidentIndex: Index, equipmentIndex: Index)
    extends JourneyDomainModel {

  override def page(userAnswers: UserAnswers): Option[Section[?]] = Some(ItemsSection(incidentIndex, equipmentIndex))
}

object ItemNumbersDomain {

  implicit def userAnswersReader(incidentIndex: Index, equipmentIndex: Index): Read[ItemNumbersDomain] = {

    val itemsReader: Read[Seq[ItemNumberDomain]] =
      ItemsSection(incidentIndex, equipmentIndex).arrayReader.to {
        case x if x.isEmpty =>
          ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, Index(0)).toSeq
        case x =>
          x.traverse[ItemNumberDomain](ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, _).apply(_))
      }

    itemsReader.map(ItemNumbersDomain.apply(_)(incidentIndex, equipmentIndex))
  }
}
