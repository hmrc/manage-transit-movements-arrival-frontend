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

package models.journeyDomain.incident.equipment

import base.SpecBase
import config.Constants.IncidentCode._
import generators.Generators
import models.journeyDomain.incident.equipment.itemNumber.ItemNumbersDomain
import models.journeyDomain.incident.equipment.seal.SealsDomain
import models.reference.IncidentCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.{AddGoodsItemNumberYesNoPage, AddSealsYesNoPage, ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import pages.incident.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.incident.{EquipmentSection, EquipmentsSection}

class EquipmentsDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "Equipments Domain" - {
    "can be parsed from user answers" - {
      "when incident code is 1 or 5" in {
        forAll(Gen.oneOf(IncidentCode(DeviatedFromItineraryCode, "test"), IncidentCode(CarrierUnableToComplyCode, "test"))) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), incidentCode)

            val expectedResult = EquipmentsDomain(Nil)(incidentIndex)

            val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              IncidentCodePage(incidentIndex)
            )
        }
      }

      "when incident code is 3 or 6" - {
        "and container indicator is false" - {
          "and add transport equipment is false" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), false)

                val expectedResult = EquipmentsDomain(Nil)(incidentIndex)

                val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  AddTransportEquipmentPage(incidentIndex)
                )
            }
          }

          "and add transport equipment is true" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode), Gen.alphaNumStr) {
              (incidentCode, containerId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                  .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                  .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val expectedResult = EquipmentsDomain(
                  value = Seq(
                    EquipmentDomain(
                      Some(containerId),
                      SealsDomain(
                        Nil
                      )(incidentIndex, equipmentIndex),
                      ItemNumbersDomain(
                        Nil
                      )(incidentIndex, equipmentIndex)
                    )(incidentIndex, equipmentIndex)
                  )
                )(incidentIndex)

                val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                  AddSealsYesNoPage(incidentIndex, equipmentIndex),
                  AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                  EquipmentSection(incidentIndex, equipmentIndex),
                  EquipmentsSection(incidentIndex)
                )
            }
          }
        }
      }
    }

    "can not be parsed from user answers" - {
      "when incident code is unanswered" in {
        val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe IncidentCodePage(incidentIndex)
        result.left.value.pages mustBe Seq(
          IncidentCodePage(incidentIndex)
        )
      }

      "when incident code is 3 or 6" - {
        "and container indicator is unanswered" in {
          forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)

              val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

              result.left.value.page mustBe ContainerIndicatorYesNoPage(incidentIndex)
              result.left.value.pages mustBe Seq(
                IncidentCodePage(incidentIndex),
                ContainerIndicatorYesNoPage(incidentIndex)
              )
          }
        }

        "and container indicator is false" - {
          "and add transport equipment is unanswered" in {
            forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

                val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe AddTransportEquipmentPage(incidentIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  AddTransportEquipmentPage(incidentIndex)
                )
            }
          }

          "and add transport equipment is true" in {
            forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)

                val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  AddTransportEquipmentPage(incidentIndex),
                  ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
                )
            }
          }
        }

        "and container indicator is true" - {
          "and container identification number is unanswered" in {
            forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)

                val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
                )
            }
          }
        }
      }

      "when incident code is 2 or 4" - {
        "and add container id number yes/no is unanswered" in {
          forAll(Gen.oneOf(IncidentCode(SealsBrokenOrTamperedCode, "test"), IncidentCode(PartiallyOrFullyUnloadedCode, "test"))) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)

              val result = EquipmentsDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
              result.left.value.pages mustBe Seq(
                IncidentCodePage(incidentIndex),
                ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
              )
          }
        }
      }
    }
  }
}
