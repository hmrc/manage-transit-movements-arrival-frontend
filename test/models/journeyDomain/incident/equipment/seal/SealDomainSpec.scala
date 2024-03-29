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
import org.scalacheck.Gen
import pages.incident.equipment.seal.SealIdentificationNumberPage

class SealDomainSpec extends SpecBase {

  "Seal Domain" - {

    "can be read from user answers" - {
      "when seal identification page is answered" in {
        val idNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), idNumber)

        val expectedResult = SealDomain(idNumber)(incidentIndex, equipmentIndex, sealIndex)

        val result = SealDomain.userAnswersReader(incidentIndex, equipmentIndex, sealIndex).apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
        )
      }
    }

    "can not be read from user answers" - {
      "when seal identification page is unanswered" in {
        val result = SealDomain.userAnswersReader(incidentIndex, equipmentIndex, sealIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
        result.left.value.pages mustBe Seq(
          SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
        )
      }
    }
  }
}
