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

package viewModels.incident

import base.SpecBase
import controllers.incident.equipment._
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.incident.IncidentCode
import models.{Index, Mode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.{AddGoodsItemNumberYesNoPage, AddSealsYesNoPage, ContainerIdentificationNumberYesNoPage}
import pages.incident.{ContainerIndicatorYesNoPage, IncidentCodePage}
import viewModels.incident.EquipmentAnswersViewModel.EquipmentAnswersViewModelProvider
import viewModels.sections.Section

class EquipmentAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "must return 3 sections" in {
    forAll(arbitraryEquipmentAnswers(emptyUserAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
      (userAnswers, mode) =>
        val sections = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
        sections.size mustBe 3
    }
  }

  "container section" - {
    val sectionTitle = "Container"

    "when incident code is 3 or 6" - {
      "and container indicator is false" - {
        "when adding container id" - {
          "must have 2 rows" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val initialAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

                forAll(arbitraryEquipmentAnswers(initialAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
                  (userAnswers, mode) =>
                    val sections         = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                    val equipmentSection = sections.head
                    equipmentSection.sectionTitle.get mustBe sectionTitle
                    equipmentSection.rows.size mustBe 2
                    equipmentSection.addAnotherLink must not be defined
                }
            }
          }
        }

        "when not adding container id" - {
          "must have 1 row" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val initialAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

                forAll(arbitraryEquipmentAnswers(initialAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
                  (userAnswers, mode) =>
                    val sections         = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                    val equipmentSection = sections.head
                    equipmentSection.sectionTitle.get mustBe sectionTitle
                    equipmentSection.rows.size mustBe 1
                    equipmentSection.addAnotherLink must not be defined
                }
            }
          }
        }
      }

      "and container indicator is true" - {
        "must have 1 row" in {
          forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
            incidentCode =>
              val initialAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)
                .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)

              forAll(arbitraryEquipmentAnswers(initialAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val sections         = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                  val equipmentSection = sections.head
                  equipmentSection.sectionTitle.get mustBe sectionTitle
                  equipmentSection.rows.size mustBe 1
                  equipmentSection.addAnotherLink must not be defined
              }
          }
        }
      }
    }

    "when incident code is 2 or 4" - {
      "when adding container id" - {
        "must have 2 rows" in {
          forAll(arbitrary[IncidentCode](arbitrary2Or4IncidentCode)) {
            incidentCode =>
              val initialAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)
                .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

              forAll(arbitraryEquipmentAnswers(initialAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val sections         = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                  val equipmentSection = sections.head
                  equipmentSection.sectionTitle.get mustBe sectionTitle
                  equipmentSection.rows.size mustBe 2
                  equipmentSection.addAnotherLink must not be defined
              }
          }
        }
      }

      "when not adding container id" - {
        "must have 1 row" in {
          forAll(arbitrary[IncidentCode](arbitrary2Or4IncidentCode)) {
            incidentCode =>
              val initialAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)
                .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

              forAll(arbitraryEquipmentAnswers(initialAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val sections         = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                  val equipmentSection = sections.head
                  equipmentSection.sectionTitle.get mustBe sectionTitle
                  equipmentSection.rows.size mustBe 1
                  equipmentSection.addAnotherLink must not be defined
              }
          }
        }
      }
    }
  }

  "seals section" - {
    val sectionTitle = "Seals"

    def checkAddAnotherLink(section: Section, userAnswers: UserAnswers, mode: Mode): Assertion = {
      val addOrRemoveSealsLink = section.addAnotherLink.value
      addOrRemoveSealsLink.text mustBe "Add or remove seals"
      addOrRemoveSealsLink.id mustBe "add-or-remove-seals"
      addOrRemoveSealsLink.href mustBe
        seal.routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
    }

    "when adding seals" - {
      "must return 1 row plus a row for each seal" in {
        forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxSeals)) {
          (mode, numberOfSeals) =>
            val initialAnswers = emptyUserAnswers.setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
            val userAnswersGen = (0 until numberOfSeals).foldLeft(Gen.const(initialAnswers)) {
              (acc, i) =>
                acc.flatMap(arbitrarySealAnswers(_, incidentIndex, equipmentIndex, Index(i)))
            }
            forAll(userAnswersGen) {
              userAnswers =>
                val sections     = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                val sealsSection = sections(1)
                sealsSection.sectionTitle.get mustBe sectionTitle
                sealsSection.rows.size mustBe 1 + numberOfSeals
                checkAddAnotherLink(sealsSection, userAnswers, mode)
            }
        }
      }
    }

    "when not adding seals" - {
      "must return 1 row" in {
        val userAnswers = emptyUserAnswers.setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)
        forAll(arbitrary[Mode]) {
          mode =>
            val sections     = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
            val sealsSection = sections(1)
            sealsSection.sectionTitle.get mustBe sectionTitle
            sealsSection.rows.size mustBe 1
            checkAddAnotherLink(sealsSection, userAnswers, mode)
        }
      }
    }
  }

  "goods item numbers section" - {
    val sectionTitle = "Goods item numbers"

    def checkAddAnotherLink(section: Section, userAnswers: UserAnswers, mode: Mode): Assertion = {
      val addOrRemoveGoodsItemNumbersLink = section.addAnotherLink.value
      addOrRemoveGoodsItemNumbersLink.text mustBe "Add or remove goods item numbers"
      addOrRemoveGoodsItemNumbersLink.id mustBe "add-or-remove-goods-item-numbers"
      addOrRemoveGoodsItemNumbersLink.href mustBe
        itemNumber.routes.AddAnotherItemNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
    }

    "when adding goods item numbers" - {
      "must return 1 row plus a row for each goods item number" in {
        forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxNumberOfItems)) {
          (mode, numberOfGoodsItemNumbers) =>
            val initialAnswers = emptyUserAnswers.setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), true)
            val userAnswersGen = (0 until numberOfGoodsItemNumbers).foldLeft(Gen.const(initialAnswers)) {
              (acc, i) =>
                acc.flatMap(arbitraryItemNumberAnswers(_, incidentIndex, equipmentIndex, Index(i)))
            }
            forAll(userAnswersGen) {
              userAnswers =>
                val sections                = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
                val goodsItemNumbersSection = sections(2)
                goodsItemNumbersSection.sectionTitle.get mustBe sectionTitle
                goodsItemNumbersSection.rows.size mustBe 1 + numberOfGoodsItemNumbers
                checkAddAnotherLink(goodsItemNumbersSection, userAnswers, mode)
            }
        }
      }
    }

    "when not adding goods item numbers" - {
      "must return 1 row" in {
        val userAnswers = emptyUserAnswers.setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)
        forAll(arbitrary[Mode]) {
          mode =>
            val sections                = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
            val goodsItemNumbersSection = sections(2)
            goodsItemNumbersSection.sectionTitle.get mustBe sectionTitle
            goodsItemNumbersSection.rows.size mustBe 1
            checkAddAnotherLink(goodsItemNumbersSection, userAnswers, mode)
        }
      }
    }
  }
}
