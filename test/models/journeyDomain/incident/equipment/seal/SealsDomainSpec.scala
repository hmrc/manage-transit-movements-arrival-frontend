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

import base.SpecBase
import models.Index
import pages.incident.equipment.seal.SealIdentificationNumberPage

class SealsDomainSpec extends SpecBase {

  "Seals Domain" - {
    "can be read from user answers" - {
      "when there are seals" in {
        val userAnswers = emptyUserAnswers
          .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), "foo")
          .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(1)), "bar")

        val expectedResult = SealsDomain(
          Seq(
            SealDomain("foo")(incidentIndex, equipmentIndex, Index(0)),
            SealDomain("bar")(incidentIndex, equipmentIndex, Index(1))
          )
        )

        val result = SealsDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
      }
    }

    "cannot be read from user answers" - {
      "when there are no seals" in {
        val result = SealsDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0))
      }
    }
  }
}
