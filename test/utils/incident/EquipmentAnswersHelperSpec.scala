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
import controllers.incident.equipment.{routes => equipmentRoutes}
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.incident.equipment.{AddSealsYesNoPage, ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}

class EquipmentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "EquipmentAnswersHelper" - {

    "seal" - {
      "must return None" - {
        "when seal is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.seal(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when seal is  defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (idNumber, mode) =>
              val userAnswers = emptyUserAnswers
                .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), idNumber)

              val helper = EquipmentAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.seal(index).get

              result.key.value mustBe "Seal 1"
              result.value.value mustBe idNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, sealIndex).url
              action.visuallyHiddenText.get mustBe "seal 1"
              action.id mustBe "change-seal-1"
          }
        }
      }
    }

    "containerIdentificationNumberYesNo" - {
      "must return None" - {
        "when ContainerIdentificationNumberYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.containerIdentificationNumberYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ContainerIdentificationNumberYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

              val helper = EquipmentAnswersHelper(answers, mode, incidentIndex, equipmentIndex)
              val result = helper.containerIdentificationNumberYesNo.get

              result.key.value mustBe "Do you want to add a container identification number?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.ContainerIdentificationNumberYesNoController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add a container identification number"
              action.id mustBe "change-add-container-identification-number"
          }
        }
      }
    }

    "containerIdentificationNumber" - {
      "must return None" - {
        "when ContainerIdentificationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.containerIdentificationNumber
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ContainerIdentificationNumberPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (containerNumber, mode) =>
              val answers = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerNumber)

              val helper = EquipmentAnswersHelper(answers, mode, incidentIndex, equipmentIndex)
              val result = helper.containerIdentificationNumber.get

              result.key.value mustBe "Container identification number"
              result.value.value mustBe containerNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.ContainerIdentificationNumberController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
              action.visuallyHiddenText.get mustBe "container identification number"
              action.id mustBe "change-container-identification-number"
          }
        }
      }

      "sealsYesNo" - {
        "must return None" - {
          "when AddSealsYesNoPage is undefined" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
                val result = helper.sealsYesNo
                result mustBe None
            }
          }
        }

        "must return Some(Row)" - {
          "when AddSealsYesNoPage defined" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val answers = emptyUserAnswers.setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)

                val helper = EquipmentAnswersHelper(answers, mode, incidentIndex, equipmentIndex)
                val result = helper.sealsYesNo.get

                result.key.value mustBe s"Do you want to add a seal for container {0}"
                result.value.value mustBe "Yes"
                val actions = result.actions.get.items
                actions.size mustBe 1
                val action = actions.head
                action.content.value mustBe "Change"
                action.href mustBe equipmentRoutes.AddSealsYesNoController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
                action.visuallyHiddenText.get mustBe "if you want to add seals"
                action.id mustBe "change-add-seals"
            }
          }
        }
      }
    }
  }
}
