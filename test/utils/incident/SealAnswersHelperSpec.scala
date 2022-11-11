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

package utils.incident

import base.SpecBase
import controllers.incident.equipment.seal.routes
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.seal.SealIdentificationNumberPage

class SealAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "SealAnswersHelper" - {

    "sealIdentificationNumber" - {

      "must return None" - {
        "when SealIdentificationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = SealAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex, sealIndex)
              val result = helper.sealIdentificationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when SealIdentificationNumberPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (sealNumber, mode) =>
              val answers = emptyUserAnswers.setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealNumber)

              val helper = SealAnswersHelper(answers, mode, incidentIndex, equipmentIndex, sealIndex)
              val result = helper.sealIdentificationNumber.get

              result.key.value mustBe "Seal identification number"
              result.value.value mustBe sealNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.SealIdentificationNumberController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex, sealIndex).url
              action.visuallyHiddenText.get mustBe "seal identification number"
              action.id mustBe "change-seal-identification-number"
          }
        }
      }
    }

  }
}
