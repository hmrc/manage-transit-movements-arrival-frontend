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

package viewModels.incident

import base.SpecBase
import generators.Generators
import models.incident.IncidentCode
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.{AddEndorsementPage, AddTransportEquipmentPage, ContainerIndicatorYesNoPage, IncidentCodePage}
import viewModels.incident.IncidentAnswersViewModel.IncidentAnswersViewModelProvider

class IncidentAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "must return 4 sections" in {
    forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex), arbitrary[Mode]) {
      (userAnswers, mode) =>
        val sections = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
        sections.size mustBe 4
    }
  }

  "incident section" - {
    "when incident code is 3 or 6" - {
      "must have 6 rows" in {
        forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
          incidentCode =>
            val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)
            forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
              (userAnswers, mode) =>
                val sections        = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                val incidentSection = sections.head
                incidentSection.sectionTitle must not be defined
                incidentSection.rows.size mustBe 6
                incidentSection.addAnotherLink must not be defined
            }
        }
      }
    }

    "when incident code is not 3 or 6" - {
      "must have 5 rows" in {
        forAll(arbitrary[IncidentCode](arbitraryNot3Or6IncidentCode)) {
          incidentCode =>
            val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)
            forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
              (userAnswers, mode) =>
                val sections        = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                val incidentSection = sections.head
                incidentSection.sectionTitle must not be defined
                incidentSection.rows.size mustBe 5
                incidentSection.addAnotherLink must not be defined
            }
        }
      }
    }
  }

  "endorsement section" - {
    val sectionTitle = "Endorsements"

    "when adding an endorsement" - {
      "must have 5 rows" in {
        val initialAnswers = emptyUserAnswers.setValue(AddEndorsementPage(incidentIndex), true)
        forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
          (userAnswers, mode) =>
            val sections           = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
            val endorsementSection = sections(1)
            endorsementSection.sectionTitle.get mustBe sectionTitle
            endorsementSection.rows.size mustBe 5
            endorsementSection.addAnotherLink must not be defined
        }
      }
    }

    "when not adding an endorsement" - {
      "must have 1 row" in {
        val initialAnswers = emptyUserAnswers.setValue(AddEndorsementPage(incidentIndex), false)
        forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
          (userAnswers, mode) =>
            val sections           = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
            val endorsementSection = sections(1)
            endorsementSection.sectionTitle.get mustBe sectionTitle
            endorsementSection.rows.size mustBe 1
            endorsementSection.addAnotherLink must not be defined
        }
      }
    }
  }

  "equipments section" - {
    val sectionTitle = "Transport equipment"

    "when incident code is 1 or 5" - {
      "must have 0 rows" in {
        forAll(arbitrary[IncidentCode](arbitrary1Or5IncidentCode)) {
          incidentCode =>
            val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)
            forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
              (userAnswers, mode) =>
                val sections          = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                val equipmentsSection = sections(2)
                equipmentsSection.sectionTitle.get mustBe sectionTitle
                equipmentsSection.rows.size mustBe 0
                equipmentsSection.addAnotherLink must not be defined
            }
        }
      }
    }

    "when incident code is 3 or 6" - {
      "and container indicator is false" - {
        "and add transport is false" - {
          "must have 1 row" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val initialAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), false)

                forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
                  (userAnswers, mode) =>
                    val sections          = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                    val equipmentsSection = sections(2)
                    equipmentsSection.sectionTitle.get mustBe sectionTitle
                    equipmentsSection.rows.size mustBe 1
                    equipmentsSection.addAnotherLink must not be defined
                }
            }
          }
        }

        "and add transport is true" - {
          "must have 1 row plus a row for each transport equipment" in {
            forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxTransportEquipments), arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              (mode, numberOfTransportEquipments, incidentCode) =>
                val initialAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)

                val userAnswersGen = (0 until numberOfTransportEquipments).foldLeft(Gen.const(initialAnswers)) {
                  (acc, i) =>
                    acc.flatMap(arbitraryEquipmentAnswers(_, incidentIndex, Index(i)))
                }
                forAll(userAnswersGen) {
                  userAnswers =>
                    val sections          = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                    val equipmentsSection = sections(2)
                    equipmentsSection.sectionTitle.get mustBe sectionTitle
                    equipmentsSection.rows.size mustBe 1 + numberOfTransportEquipments
                    equipmentsSection.addAnotherLink must be(defined)
                }
            }
          }
        }
      }

      "when incident code is 2 or 4" - {
        "must have a row for each transport equipment" in {
          forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxTransportEquipments), arbitrary[IncidentCode](arbitrary2Or4IncidentCode)) {
            (mode, numberOfTransportEquipments, incidentCode) =>
              val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)
              val userAnswersGen = (0 until numberOfTransportEquipments).foldLeft(Gen.const(initialAnswers)) {
                (acc, i) =>
                  acc.flatMap(arbitraryEquipmentAnswers(_, incidentIndex, Index(i)))
              }
              forAll(userAnswersGen) {
                userAnswers =>
                  val sections          = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                  val equipmentsSection = sections(2)
                  equipmentsSection.sectionTitle.get mustBe sectionTitle
                  equipmentsSection.rows.size mustBe numberOfTransportEquipments
                  equipmentsSection.addAnotherLink must be(defined)
              }
          }
        }
      }
    }

    "transport means section" - {
      val sectionTitle = "Replacement means of transport"

      "when incident code is 3 or 6" - {
        "must have 3 rows" in {
          forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
            incidentCode =>
              val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)
              forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val sections              = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                  val transportMeansSection = sections(3)
                  transportMeansSection.sectionTitle.get mustBe sectionTitle
                  transportMeansSection.rows.size mustBe 3
                  transportMeansSection.addAnotherLink must not be defined
              }
          }
        }
      }

      "when incident code is not 3 or 6" - {
        "must have 0 rows" in {
          forAll(arbitrary[IncidentCode](arbitraryNot3Or6IncidentCode)) {
            incidentCode =>
              val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)
              forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex), arbitrary[Mode]) {
                (userAnswers, mode) =>
                  val sections              = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
                  val transportMeansSection = sections(3)
                  transportMeansSection.sectionTitle.get mustBe sectionTitle
                  transportMeansSection.rows.size mustBe 0
                  transportMeansSection.addAnotherLink must not be defined
              }
          }
        }
      }
    }
  }
}
