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

import base.SpecBase
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.incident.equipment.itemNumber.ItemNumberPage

class ItemNumberDomainSpec extends SpecBase {

  "Item Number Domain" - {

    "can be read from user answers" - {
      "when item number page is answered" in {
        val itemNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), itemNumber)

        val expectedResult = ItemNumberDomain(itemNumber)(incidentIndex, equipmentIndex, itemNumberIndex)

        val result: EitherType[ItemNumberDomain] =
          UserAnswersReader[ItemNumberDomain](ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, itemNumberIndex)).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "can not be read from user answers" - {
      "when item number page is unanswered" in {
        val result: EitherType[ItemNumberDomain] =
          UserAnswersReader[ItemNumberDomain](ItemNumberDomain.userAnswersReader(incidentIndex, equipmentIndex, itemNumberIndex)).run(emptyUserAnswers)

        result.left.value.page mustBe ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex)
      }
    }
  }

}
