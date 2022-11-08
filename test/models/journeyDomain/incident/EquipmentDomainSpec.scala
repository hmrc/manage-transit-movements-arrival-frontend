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

package models.journeyDomain.incident

import base.SpecBase
import generators.Generators
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class EquipmentDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  private val containerId = Gen.alphaNumStr.sample.value

  "EquipmentDomain" - {

    "can be parsed from user answers" - {

      /*"when incident code is 2 or 4" - {
        "and container id is answered" in {
          forAll(Gen.oneOf(IncidentCode.SealsBrokenOrTampered, IncidentCode.PartiallyOrFullyUnloaded)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)
                .setValue(ContainerIdentificationNumberYesNoPage(index), true)
                .setValue(ContainerIdentificationNumberPage(index), containerId)

              val expectedResult = Some(
                EquipmentDomain(
                  containerId = Some(containerId)
                )
              )

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.value mustBe expectedResult
          }
        }
      }

      "when incident code is 3 or 6" - {
        "and container indicator is true" in {
          forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)
                .setValue(ContainerIndicatorYesNoPage(index), true)
                .setValue(ContainerIdentificationNumberPage(index), containerId)

              val expectedResult = Some(
                EquipmentDomain(
                  containerId = Some(containerId)
                )
              )

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.value mustBe expectedResult
          }
        }

        "and container indicator is false" - {
          "and not adding transport equipment" in {
            forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(index), incidentCode)
                  .setValue(incident.ContainerIndicatorYesNoPage(index), false)
                  .setValue(AddTransportEquipmentPage(index), false)

                val expectedResult = Some(
                  EquipmentDomain(
                    containerId = None
                  )
                )

                val result: EitherType[Option[EquipmentDomain]] =
                  UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

                result.value mustBe expectedResult
            }
          }

          "and adding transport equipment" - {
            "and adding container id" in {
              forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(index), incidentCode)
                    .setValue(incident.ContainerIndicatorYesNoPage(index), false)
                    .setValue(incident.AddTransportEquipmentPage(index), true)
                    .setValue(ContainerIdentificationNumberYesNoPage(index), true)
                    .setValue(ContainerIdentificationNumberPage(index), containerId)

                  val expectedResult = Some(
                    EquipmentDomain(
                      containerId = Some(containerId)
                    )
                  )

                  val result: EitherType[Option[EquipmentDomain]] =
                    UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

                  result.value mustBe expectedResult
              }
            }
          }
        }
      }

      "when incident code is 1 or 5" in {
        forAll(Gen.oneOf(IncidentCode.DeviatedFromItinerary, IncidentCode.CarrierUnableToComply)) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(index), incidentCode)

            val expectedResult = None

            val result: EitherType[Option[EquipmentDomain]] =
              UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

            result.value mustBe expectedResult
        }
      }*/
    }

    "cannot be parsed from user answers" - {
      /*"when incident code is 2 or 4" - {
        "and add container id yes/no is unanswered" in {
          forAll(Gen.oneOf(IncidentCode.SealsBrokenOrTampered, IncidentCode.PartiallyOrFullyUnloaded)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers.setValue(IncidentCodePage(index), incidentCode)

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(index)
          }
        }

        "and add container id yes/no is yes and container id is unanswered" in {
          forAll(Gen.oneOf(IncidentCode.SealsBrokenOrTampered, IncidentCode.PartiallyOrFullyUnloaded)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)
                .setValue(ContainerIdentificationNumberYesNoPage(index), true)

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberPage(index)
          }
        }
      }

      "when incident code is 3 or 6" - {
        "and container indicator is unanswered" in {
          forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.left.value.page mustBe incident.ContainerIndicatorYesNoPage(index)
          }
        }

        "and container indicator is true and container id is unanswered" in {
          forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)
                .setValue(incident.ContainerIndicatorYesNoPage(index), true)

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberPage(index)
          }
        }

        "and container indicator is false and add equipment yes/no is unanswered" in {
          forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)
                .setValue(incident.ContainerIndicatorYesNoPage(index), false)

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.left.value.page mustBe incident.AddTransportEquipmentPage(index)
          }
        }

        "and container indicator is false, equipment yes/no is true and add container id yes/no is unanswered" in {
          forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(index), incidentCode)
                .setValue(incident.ContainerIndicatorYesNoPage(index), false)
                .setValue(incident.AddTransportEquipmentPage(index), true)

              val result: EitherType[Option[EquipmentDomain]] =
                UserAnswersReader[Option[EquipmentDomain]](EquipmentDomain.userAnswersReader(index)).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(index)
          }
        }
      }*/
    }
  }

}
