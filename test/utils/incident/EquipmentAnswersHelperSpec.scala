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
import controllers.incident.equipment.itemNumber.{routes => goodsItemNumberRoutes}
import controllers.incident.equipment.seal.{routes => sealRoutes}
import controllers.incident.equipment.itemNumber.{routes => itemRoutes}
import controllers.incident.equipment.{routes => equipmentRoutes}
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.itemNumber.ItemNumberPage
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.incident.equipment.{AddGoodsItemNumberYesNoPage, AddSealsYesNoPage, ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import pages.sections.incident.{ItemSection, SealSection}
import play.api.libs.json.Json

class EquipmentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "EquipmentAnswersHelper" - {

    "seals" - {
      "must return no rows" - {
        "when no seals defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.seals
              result mustBe Nil
          }
        }
      }

      "must return rows" - {
        "when seals defined" in {
          forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxSeals)) {
            (mode, count) =>
              val userAnswersGen = (0 until count).foldLeft(Gen.const(emptyUserAnswers)) {
                (acc, i) =>
                  acc.flatMap(arbitrarySealAnswers(_, incidentIndex, equipmentIndex, Index(i)))
              }
              forAll(userAnswersGen) {
                userAnswers =>
                  val helper = EquipmentAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                  val result = helper.seals
                  result.size mustBe count
              }
          }
        }
      }
    }

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
        "when seal is defined" in {
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
              action.href mustBe sealRoutes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, sealIndex).url
              action.visuallyHiddenText.get mustBe "seal 1"
              action.id mustBe "change-seal-1"
          }
        }
      }
    }

    "addOrRemoveSeals" - {
      "must return None" - {
        "when seals array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.addOrRemoveSeals
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when seals array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(SealSection(incidentIndex, equipmentIndex, Index(0)), Json.obj("foo" -> "bar"))
              val helper  = EquipmentAnswersHelper(answers, mode, incidentIndex, equipmentIndex)
              val result  = helper.addOrRemoveSeals.get

              result.id mustBe "add-or-remove-seals"
              result.text mustBe "Add or remove seals"
              result.href mustBe sealRoutes.AddAnotherSealController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
          }
        }
      }
    }

    "goodsItemNumbers" - {
      "must return no rows" - {
        "when no goods item numbers defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.goodsItemNumbers
              result mustBe Nil
          }
        }
      }

      "must return rows" - {
        "when goods item numbers defined" in {
          forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxNumberOfItems)) {
            (mode, count) =>
              val userAnswersGen = (0 until count).foldLeft(Gen.const(emptyUserAnswers)) {
                (acc, i) =>
                  acc.flatMap(arbitraryItemNumberAnswers(_, incidentIndex, equipmentIndex, Index(i)))
              }
              forAll(userAnswersGen) {
                userAnswers =>
                  val helper = EquipmentAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                  val result = helper.goodsItemNumbers
                  result.size mustBe count
              }
          }
        }
      }
    }

    "goodsItemNumber" - {
      "must return None" - {
        "when goods item number is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.goodsItemNumber(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when goods item number is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (goodsItemNumber, mode) =>
              val userAnswers = emptyUserAnswers
                .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), goodsItemNumber)

              val helper = EquipmentAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.goodsItemNumber(index).get

              result.key.value mustBe "Goods item number 1"
              result.value.value mustBe goodsItemNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe goodsItemNumberRoutes.ItemNumberController
                .onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex)
                .url
              action.visuallyHiddenText.get mustBe "goods item number 1"
              action.id mustBe "change-goods-item-number-1"
          }
        }
      }
    }

    "addOrRemoveGoodsItemNumber" - {
      "must return None" - {
        "when goodsItemNumber array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
              val result = helper.addOrRemoveGoodsItemNumber
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when goodsItemNumber array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ItemSection(incidentIndex, equipmentIndex, Index(0)), Json.obj("foo" -> "bar"))
              val helper  = EquipmentAnswersHelper(answers, mode, incidentIndex, equipmentIndex)
              val result  = helper.addOrRemoveGoodsItemNumber.get

              result.id mustBe "add-or-remove-goods-item-numbers"
              result.text mustBe "Add or remove goods item numbers"
              result.href mustBe itemRoutes.AddAnotherItemNumberYesNoController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
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

              result.key.value mustBe "Do you want to add an identification number for the new container?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.ContainerIdentificationNumberYesNoController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
              action.visuallyHiddenText.get mustBe "if you want to add an identification number for the new container"
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

                result.key.value mustBe "Did any seals change?"
                result.value.value mustBe "Yes"
                val actions = result.actions.get.items
                actions.size mustBe 1
                val action = actions.head
                action.content.value mustBe "Change"
                action.href mustBe equipmentRoutes.AddSealsYesNoController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
                action.visuallyHiddenText.get mustBe "if any seals changed"
                action.id mustBe "change-add-seals"
            }
          }
        }
      }

      "goodsItemNumbersYesNo" - {
        "must return None" - {
          "when AddGoodsItemNumberYesNoPage is undefined" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val helper = EquipmentAnswersHelper(emptyUserAnswers, mode, incidentIndex, equipmentIndex)
                val result = helper.goodsItemNumbersYesNo
                result mustBe None
            }
          }
        }

        "must return Some(Row)" - {
          "when AddGoodsItemNumberYesNoPage defined" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val answers = emptyUserAnswers.setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), true)

                val helper = EquipmentAnswersHelper(answers, mode, incidentIndex, equipmentIndex)
                val result = helper.goodsItemNumbersYesNo.get

                result.key.value mustBe "Do you want to add a goods item number?"
                result.value.value mustBe "Yes"
                val actions = result.actions.get.items
                actions.size mustBe 1
                val action = actions.head
                action.content.value mustBe "Change"
                action.href mustBe equipmentRoutes.AddGoodsItemNumberYesNoController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex).url
                action.visuallyHiddenText.get mustBe "if you want to add a goods item number"
                action.id mustBe "change-add-goods-item-numbers"
            }
          }
        }
      }
    }
  }
}
