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
import models.Index
import models.journeyDomain.incident.equipment.itemNumber.{ItemNumberDomain, ItemNumbersDomain}
import models.journeyDomain.incident.equipment.seal.{SealDomain, SealsDomain}
import models.reference.IncidentCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment._
import pages.incident.equipment.itemNumber.ItemNumberPage
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.incident.{ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.incident.{EquipmentSection, ItemsSection, SealsSection}

class EquipmentDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  private val containerId     = Gen.alphaNumStr.sample.value
  private val sealId          = Gen.alphaNumStr.sample.value
  private val goodsItemNumber = Gen.alphaNumStr.sample.value

  "EquipmentDomain" - {

    "can be parsed from user answers" - {

      "when incident code is 3 or 6" - {

        "when container indicator is true" - {

          "and container id is answered" - {

            "and adding seals" in {
              forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                    .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                    .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
                    .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                  val expectedResult = EquipmentDomain(
                    Some(containerId),
                    SealsDomain(
                      Seq(
                        SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                      )
                    )(incidentIndex, equipmentIndex),
                    ItemNumbersDomain(
                      Nil
                    )(incidentIndex, equipmentIndex)
                  )(incidentIndex, equipmentIndex)

                  val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    IncidentCodePage(incidentIndex),
                    ContainerIndicatorYesNoPage(incidentIndex),
                    ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                    AddSealsYesNoPage(incidentIndex, equipmentIndex),
                    SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
                    SealsSection(incidentIndex, equipmentIndex),
                    AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                    EquipmentSection(incidentIndex, equipmentIndex)
                  )
              }
            }

            "and not adding seals" in {
              forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                    .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)
                    .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                  val expectedResult = EquipmentDomain(
                    Some(containerId),
                    SealsDomain(
                      Nil
                    )(incidentIndex, equipmentIndex),
                    ItemNumbersDomain(
                      Nil
                    )(incidentIndex, equipmentIndex)
                  )(incidentIndex, equipmentIndex)

                  val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    IncidentCodePage(incidentIndex),
                    ContainerIndicatorYesNoPage(incidentIndex),
                    ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                    AddSealsYesNoPage(incidentIndex, equipmentIndex),
                    AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                    EquipmentSection(incidentIndex, equipmentIndex)
                  )
              }
            }
          }
        }

        "when container indicator is false" - {
          "and container id is answered" - {

            "and adding seals" in {
              forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                    .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                    .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                    .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
                    .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                  val expectedResult = EquipmentDomain(
                    Some(containerId),
                    SealsDomain(
                      Seq(
                        SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                      )
                    )(incidentIndex, equipmentIndex),
                    ItemNumbersDomain(
                      Nil
                    )(incidentIndex, equipmentIndex)
                  )(incidentIndex, equipmentIndex)

                  val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    IncidentCodePage(incidentIndex),
                    ContainerIndicatorYesNoPage(incidentIndex),
                    ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                    ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                    AddSealsYesNoPage(incidentIndex, equipmentIndex),
                    SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
                    SealsSection(incidentIndex, equipmentIndex),
                    AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                    EquipmentSection(incidentIndex, equipmentIndex)
                  )
              }
            }

            "and not adding seals" in {
              forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
                incidentCode =>
                  val userAnswers = emptyUserAnswers
                    .setValue(IncidentCodePage(incidentIndex), incidentCode)
                    .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                    .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                    .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                    .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)
                    .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                  val expectedResult = EquipmentDomain(
                    Some(containerId),
                    SealsDomain(
                      Nil
                    )(incidentIndex, equipmentIndex),
                    ItemNumbersDomain(
                      Nil
                    )(incidentIndex, equipmentIndex)
                  )(incidentIndex, equipmentIndex)

                  val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                  result.value.value mustBe expectedResult
                  result.value.pages mustBe Seq(
                    IncidentCodePage(incidentIndex),
                    ContainerIndicatorYesNoPage(incidentIndex),
                    ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                    ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                    AddSealsYesNoPage(incidentIndex, equipmentIndex),
                    AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                    EquipmentSection(incidentIndex, equipmentIndex)
                  )
              }
            }
          }

          "and container id is unanswered" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val expectedResult = EquipmentDomain(
                  None,
                  SealsDomain(
                    Seq(
                      SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                    )
                  )(incidentIndex, equipmentIndex),
                  ItemNumbersDomain(
                    Nil
                  )(incidentIndex, equipmentIndex)
                )(incidentIndex, equipmentIndex)

                val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                result.value.value mustBe expectedResult
                result.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                  SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
                  SealsSection(incidentIndex, equipmentIndex),
                  AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                  EquipmentSection(incidentIndex, equipmentIndex)
                )
            }
          }
        }
      }

      "when incident code is 2 or 4" - {

        "and container id is answered" - {
          "and incident code is 2" in {
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), IncidentCode(SealsBrokenOrTamperedCode, "test"))
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
              .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
              .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
              .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

            val expectedResult = EquipmentDomain(
              Some(containerId),
              SealsDomain(
                Seq(
                  SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                )
              )(incidentIndex, equipmentIndex),
              ItemNumbersDomain(
                Nil
              )(incidentIndex, equipmentIndex)
            )(incidentIndex, equipmentIndex)

            val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              IncidentCodePage(incidentIndex),
              ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
              ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
              SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
              SealsSection(incidentIndex, equipmentIndex),
              AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
              EquipmentSection(incidentIndex, equipmentIndex)
            )
          }

          "and incident code is 4" - {
            "and adding seals" in {
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), IncidentCode(PartiallyOrFullyUnloadedCode, "test"))
                .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
                .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
                .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

              val expectedResult = EquipmentDomain(
                Some(containerId),
                SealsDomain(
                  Seq(
                    SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                  )
                )(incidentIndex, equipmentIndex),
                ItemNumbersDomain(
                  Nil
                )(incidentIndex, equipmentIndex)
              )(incidentIndex, equipmentIndex)

              val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                IncidentCodePage(incidentIndex),
                ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                AddSealsYesNoPage(incidentIndex, equipmentIndex),
                SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
                SealsSection(incidentIndex, equipmentIndex),
                AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                EquipmentSection(incidentIndex, equipmentIndex)
              )
            }

            "and not adding seals" in {
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), IncidentCode(PartiallyOrFullyUnloadedCode, "test"))
                .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)
                .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

              val expectedResult = EquipmentDomain(
                Some(containerId),
                SealsDomain(
                  Nil
                )(incidentIndex, equipmentIndex),
                ItemNumbersDomain(
                  Nil
                )(incidentIndex, equipmentIndex)
              )(incidentIndex, equipmentIndex)

              val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                IncidentCodePage(incidentIndex),
                ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                AddSealsYesNoPage(incidentIndex, equipmentIndex),
                AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                EquipmentSection(incidentIndex, equipmentIndex)
              )
            }
          }
        }

        "and container id is not answered" in {
          forAll(arbitrary[IncidentCode](arbitrary2Or4IncidentCode)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)
                .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)
                .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
                .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

              val expectedResult = EquipmentDomain(
                None,
                SealsDomain(
                  Seq(
                    SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                  )
                )(incidentIndex, equipmentIndex),
                ItemNumbersDomain(
                  Nil
                )(incidentIndex, equipmentIndex)
              )(incidentIndex, equipmentIndex)

              val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

              result.value.value mustBe expectedResult
              result.value.pages mustBe Seq(
                IncidentCodePage(incidentIndex),
                ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
                SealsSection(incidentIndex, equipmentIndex),
                AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
                EquipmentSection(incidentIndex, equipmentIndex)
              )
          }
        }
      }

      "when adding goods item numbers" in {
        forAll(arbitrary[IncidentCode](arbitrary2Or4IncidentCode)) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), incidentCode)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)
              .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), sealId)
              .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), true)
              .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), goodsItemNumber)

            val expectedResult = EquipmentDomain(
              None,
              SealsDomain(
                Seq(
                  SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex)
                )
              )(incidentIndex, equipmentIndex),
              ItemNumbersDomain(
                Seq(
                  ItemNumberDomain(goodsItemNumber)(incidentIndex, equipmentIndex, itemNumberIndex)
                )
              )(incidentIndex, equipmentIndex)
            )(incidentIndex, equipmentIndex)

            val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

            result.value.value mustBe expectedResult
            result.value.pages mustBe Seq(
              IncidentCodePage(incidentIndex),
              ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
              SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
              SealsSection(incidentIndex, equipmentIndex),
              AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
              ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex),
              ItemsSection(incidentIndex, equipmentIndex),
              EquipmentSection(incidentIndex, equipmentIndex)
            )
        }
      }
    }

    "cannot be parsed from user answers" - {

      "when incident code is 3 or 6" - {

        "when container indicator is true" - {
          "and container id number is unanswered" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)

                val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
                )
            }
          }

          "and container id is answered" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
                  .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

                val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe AddSealsYesNoPage(incidentIndex, equipmentIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
                  AddSealsYesNoPage(incidentIndex, equipmentIndex)
                )
            }
          }
        }

        "when container indicator is false" - {
          "and add container id number is unanswered" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

                val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex)
                )
            }
          }

          "and add container id is false" in {
            forAll(arbitrary[IncidentCode](arbitrary3Or6IncidentCode)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

                result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
                result.left.value.pages mustBe Seq(
                  IncidentCodePage(incidentIndex),
                  ContainerIndicatorYesNoPage(incidentIndex),
                  ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                  SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
                )
            }
          }
        }
      }

      "when incident code is 2 or 4" - {

        "and container id number is unanswered" in {
          forAll(arbitrary[IncidentCode](arbitrary2Or4IncidentCode)) {
            incidentCode =>
              val userAnswers = emptyUserAnswers
                .setValue(IncidentCodePage(incidentIndex), incidentCode)
                .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

              val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

              result.left.value.page mustBe ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
              result.left.value.pages mustBe Seq(
                IncidentCodePage(incidentIndex),
                ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
                ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)
              )
          }
        }

        "and incident code is 2" in {
          val userAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode(SealsBrokenOrTamperedCode, "test"))
            .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
            .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

          val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

          result.left.value.page mustBe SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
          result.left.value.pages mustBe Seq(
            IncidentCodePage(incidentIndex),
            ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
            ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
            SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
          )
        }

        "and incident code is 4" in {
          val userAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode(PartiallyOrFullyUnloadedCode, "test"))
            .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
            .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

          val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

          result.left.value.page mustBe AddSealsYesNoPage(incidentIndex, equipmentIndex)
          result.left.value.pages mustBe Seq(
            IncidentCodePage(incidentIndex),
            ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
            ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
            AddSealsYesNoPage(incidentIndex, equipmentIndex)
          )
        }
      }

      "when incident code is not 2, 3, 4 or 6" in {
        forAll(arbitrary[IncidentCode](arbitrary1Or5IncidentCode)) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(IncidentCodePage(incidentIndex), incidentCode)

            val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

            result.left.value.page mustBe IncidentCodePage(incidentIndex)
            result.left.value.pages mustBe Seq(
              IncidentCodePage(incidentIndex)
            )
        }
      }

      "when add seals is no" in {
        val userAnswers = emptyUserAnswers
          .setValue(IncidentCodePage(incidentIndex), IncidentCode(PartiallyOrFullyUnloadedCode, "test"))
          .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
          .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
          .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)

        val result = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex)
        result.left.value.pages mustBe Seq(
          IncidentCodePage(incidentIndex),
          ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex),
          ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
          AddSealsYesNoPage(incidentIndex, equipmentIndex),
          AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex)
        )
      }
    }
  }
}
