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

package models.journeyDomain.incident.equipment

import base.SpecBase
import generators.Generators
import models.Index
import models.incident.IncidentCode
import models.journeyDomain.incident.seal.{SealDomain, SealsDomain}
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment._
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.incident.{ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.incident.EquipmentSection
import play.api.libs.json.Json

class EquipmentDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  private val containerId = Gen.alphaNumStr.sample.value
  private val sealId      = Gen.alphaNumStr.sample.value

  "EquipmentDomain" - {

    "can be parsed from user answers" - {

      "when container indicator is true" - {
        "and container id is answered" - {
          "and incident code is 2" in {
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
              .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
              .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)

            val expectedResult = EquipmentDomain(
              Some(containerId),
              SealsDomain(
                Seq(
                  SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                )
              )
            )(incidentIndex, equipmentIndex)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and incident code is not 2" - {
            "and adding seals" in {
              forAll(Gen.oneOf(IncidentCode.values.filterNot(_ == IncidentCode.SealsBrokenOrTampered))) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                    .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                    .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)

                  val expectedResult = EquipmentDomain(
                    Some(containerId),
                    SealsDomain(
                      Seq(
                        SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                      )
                    )
                  )(incidentIndex, equipmentIndex)

                  val result: EitherType[EquipmentDomain] =
                    UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

                  result.value mustBe expectedResult
              }
            }

            "and not adding seals" in {
              forAll(Gen.oneOf(IncidentCode.values.filterNot(_ == IncidentCode.SealsBrokenOrTampered))) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                    .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)

                  val expectedResult = EquipmentDomain(
                    Some(containerId),
                    SealsDomain(Nil)
                  )(incidentIndex, equipmentIndex)

                  val result: EitherType[EquipmentDomain] =
                    UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

                  result.value mustBe expectedResult
              }
            }
          }
        }
      }

      "when container indicator is false" - {
        "and equipment index is 0" - {
          "and container id is answered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
              .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
              .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)

            val expectedResult = EquipmentDomain(
              Some(containerId),
              SealsDomain(
                Seq(
                  SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                )
              )
            )(incidentIndex, equipmentIndex)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and container id is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)
              .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)

            val expectedResult = EquipmentDomain(
              None,
              SealsDomain(
                Seq(
                  SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                )
              )
            )(incidentIndex, equipmentIndex)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.value mustBe expectedResult
          }
        }

        "and equipment index is not 0" - {
          val equipmentIndex = Index(1)

          "and incident code is 2" in {
            val userAnswers = emptyUserAnswers
              .setValue(EquipmentSection(incidentIndex, Index(0)), Json.obj("foo" -> "bar"))
              .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
              .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)

            val expectedResult = EquipmentDomain(
              None,
              SealsDomain(
                Seq(
                  SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                )
              )
            )(incidentIndex, equipmentIndex)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and incident code is not 2" - {
            "and adding seals" in {
              forAll(Gen.oneOf(IncidentCode.values.filterNot(_ == IncidentCode.SealsBrokenOrTampered))) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                    .setValue(EquipmentSection(incidentIndex, Index(0)), Json.obj("foo" -> "bar"))
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                    .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)

                  val expectedResult = EquipmentDomain(
                    None,
                    SealsDomain(
                      Seq(
                        SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                      )
                    )
                  )(incidentIndex, equipmentIndex)

                  val result: EitherType[EquipmentDomain] =
                    UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

                  result.value mustBe expectedResult
              }
            }

            "and not adding seals" in {
              forAll(Gen.oneOf(IncidentCode.values.filterNot(_ == IncidentCode.SealsBrokenOrTampered))) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                    .setValue(EquipmentSection(incidentIndex, Index(0)), Json.obj("foo" -> "bar"))
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)

                  val expectedResult = EquipmentDomain(
                    None,
                    SealsDomain(Nil)
                  )(incidentIndex, equipmentIndex)

                  val result: EitherType[EquipmentDomain] =
                    UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

                  result.value mustBe expectedResult
              }
            }
          }
        }
      }
    }

    "cannot be parsed from user answers" - {

      "when container indicator is true" - {
        "and container id number is unanswered" in {
          val userAnswers = emptyUserAnswers.setValue(ContainerIndicatorYesNoPage(incidentIndex), true)

          val result: EitherType[EquipmentDomain] =
            UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

          result.left.value.page mustBe ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
        }

        "and incident code is 2" in {
          val userAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
            .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
            .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

          val result: EitherType[EquipmentDomain] =
            UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

          result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0))
        }

        "and incident code is not 2" in {
          forAll(Gen.oneOf(IncidentCode.values.filterNot(_ == IncidentCode.SealsBrokenOrTampered))) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)
                .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

              val result: EitherType[EquipmentDomain] =
                UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

              result.left.value.page mustBe AddSealsYesNoPage(incidentIndex, equipmentIndex)
          }
        }
      }

      "when container indicator is false" - {
        "and equipment index is 0" - {
          "and add container id number is unanswered" in {
            val userAnswers = emptyUserAnswers.setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, Index(0))).run(userAnswers)

            result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, Index(0))
          }

          "and add container id is false" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, Index(0))).run(userAnswers)

            result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0))
          }
        }

        "and equipment index is not 0" - {
          val equipmentIndex = Index(1)

          "and incident code is 2" in {
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0))
          }

          "and incident code is not 2" in {
            forAll(Gen.oneOf(IncidentCode.values.filterNot(_ == IncidentCode.SealsBrokenOrTampered))) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

                val result: EitherType[EquipmentDomain] =
                  UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

                result.left.value.page mustBe AddSealsYesNoPage(incidentIndex, equipmentIndex)
            }
          }
        }
      }
    }
  }

}
