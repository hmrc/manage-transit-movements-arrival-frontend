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

package utils.incident

import base.SpecBase
import controllers.incident.equipment.seal.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident.equipment.AddSealsYesNoPage
import pages.incident.equipment.seal.SealIdentificationNumberPage
import viewModels.ListItem

class SealsAnswersHelperSpec extends SpecBase with Generators {

  "SealsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = SealsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with a complete seal" - {
        "and add seal yes/no page is defined" - {
          "must return list items with remove links" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)

                val helper = SealsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = sealId,
                      changeUrl = routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url,
                      removeUrl = Some(routes.ConfirmRemoveSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url)
                    )
                  )
                )
            }
          }
        }

        "and add seal yes/no page is undefined" - {
          "must return list items with no remove link a index 0" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)

                val helper = SealsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    viewModels.ListItem(
                      name = sealId,
                      changeUrl = routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }
        }
      }

      "when user answers populated with complete seals" - {
        "and add seal yes/no page is defined" - {
          "must return list items with remove links" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(1)), sealId)

                val helper = SealsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = sealId,
                      changeUrl = routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url,
                      removeUrl = Some(routes.ConfirmRemoveSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url)
                    )
                  ),
                  Right(
                    viewModels.ListItem(
                      name = sealId,
                      changeUrl = routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(1)).url,
                      removeUrl = Some(routes.ConfirmRemoveSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(1)).url)
                    )
                  )
                )
            }
          }
        }

        "and add seal yes/no page is undefined" - {
          "must return list items with no remove link a index 0" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(1)), sealId)

                val helper = SealsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    viewModels.ListItem(
                      name = sealId,
                      changeUrl = routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url,
                      removeUrl = Some(routes.ConfirmRemoveSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url)
                    )
                  ),
                  Right(
                    viewModels.ListItem(
                      name = sealId,
                      changeUrl = routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(1)).url,
                      removeUrl = Some(routes.ConfirmRemoveSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(1)).url)
                    )
                  )
                )
            }
          }
        }
      }
    }
  }

}
