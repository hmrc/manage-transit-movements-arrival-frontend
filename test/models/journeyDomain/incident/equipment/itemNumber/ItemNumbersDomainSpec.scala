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

package models.journeyDomain.incident.equipment.itemNumber

import base.SpecBase
import models.Index
import models.journeyDomain.{EitherType, UserAnswersReader}
import pages.incident.equipment.itemNumber.ItemNumberPage

class ItemNumbersDomainSpec extends SpecBase {

  "Item Numbers Domain" - {
    "can be read from user answers" - {
      "when there are item numbers" in {
        val userAnswers = emptyUserAnswers
          .setValue(ItemNumberPage(incidentIndex, equipmentIndex, Index(0)), "foo")
          .setValue(ItemNumberPage(incidentIndex, equipmentIndex, Index(1)), "bar")

        val expectedResult = ItemNumbersDomain(
          Seq(
            ItemNumberDomain("foo")(incidentIndex, equipmentIndex, Index(0)),
            ItemNumberDomain("bar")(incidentIndex, equipmentIndex, Index(1))
          )
        )

        val result: EitherType[ItemNumbersDomain] =
          UserAnswersReader[ItemNumbersDomain](ItemNumbersDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be read from user answers" - {
      "when there are no item numbers" in {
        val result: EitherType[ItemNumbersDomain] =
          UserAnswersReader[ItemNumbersDomain](ItemNumbersDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(emptyUserAnswers)

        result.left.value.page mustBe ItemNumberPage(incidentIndex, equipmentIndex, Index(0))
      }
    }
  }

}
