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
import models.Index
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.IncidentCode
import org.scalacheck.Gen
import pages.incident.equipment.{ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import pages.incident.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage, IncidentCodePage}

class EquipmentsDomainSpec extends SpecBase {

  "Equipments Domain" - {
    "can be parsed from user answers" - {
      "when incident code is 1 or 5" in {
        forAll(Gen.oneOf(IncidentCode(DeviatedFromItineraryCode, "test"), IncidentCode(CarrierUnableToComplyCode, "test"))) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), incidentCode)

            val expectedResult = EquipmentsDomain(Nil)(incidentIndex)

            val result: EitherType[EquipmentsDomain] =
              UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

            result.value mustBe expectedResult
        }
      }

      "when incident code is 3 or 6" - {
        "and container indicator is false" - {
          "and add transport equipment is false" in {
            forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), false)

                val expectedResult = EquipmentsDomain(Nil)(incidentIndex)

                val result: EitherType[EquipmentsDomain] =
                  UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

                result.value mustBe expectedResult
            }
          }
        }
      }
    }

    "can not be parsed from user answers" - {
      "when incident code is unanswered" in {
        val result: EitherType[EquipmentsDomain] =
          UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(emptyUserAnswers)

        result.left.value.page mustBe IncidentCodePage(incidentIndex)
      }

      "when incident code is 3 or 6" - {
        "and container indicator is unanswered" in {
          forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)

              val result: EitherType[EquipmentsDomain] =
                UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

              result.left.value.page mustBe ContainerIndicatorYesNoPage(incidentIndex)
          }
        }

        "and container indicator is false" - {
          "and add transport equipment is unanswered" in {
            forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

                val result: EitherType[EquipmentsDomain] =
                  UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

                result.left.value.page mustBe AddTransportEquipmentPage(incidentIndex)
            }
          }

          "and add transport equipment is true" in {
            forAll(Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)

                val result: EitherType[EquipmentsDomain] =
                  UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, Index(0))
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

                val result: EitherType[EquipmentsDomain] =
                  UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberPage(incidentIndex, Index(0))
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

              val result: EitherType[EquipmentsDomain] =
                UserAnswersReader[EquipmentsDomain](EquipmentsDomain.userAnswersReader(incidentIndex)).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, Index(0))
          }
        }
      }
    }
  }

}
