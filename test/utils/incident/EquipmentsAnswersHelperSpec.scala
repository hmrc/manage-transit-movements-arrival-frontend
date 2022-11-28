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
import controllers.incident.equipment._
import generators.Generators
import models.incident.IncidentCode
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident._
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.incident.equipment.{AddGoodsItemNumberYesNoPage, AddSealsYesNoPage, ContainerIdentificationNumberPage, ContainerIdentificationNumberYesNoPage}
import viewModels.ListItem

class EquipmentsAnswersHelperSpec extends SpecBase with Generators {

  "EquipmentsAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with a complete equipment" - {

        "and add transport equipment yes/no is unanswered" - {

          "and equipment has no container id" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = "Transport equipment 1",
                      changeUrl = routes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }

          "and equipment has a container id" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr, Gen.alphaNumStr) {
              (mode, containerId, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = s"Transport equipment 1 - container $containerId",
                      changeUrl = routes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }
        }

        "and add transport equipment yes/no is answered" - {

          "and equipment has no container id" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, sealId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.TransferredToAnotherTransport)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)
                  .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(0)), sealId)
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = "Transport equipment 1",
                      changeUrl = routes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = Some(routes.ConfirmRemoveEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url)
                    )
                  )
                )
            }
          }

          "and equipment has a container id" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, containerId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.TransferredToAnotherTransport)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
                  .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = s"Transport equipment 1 - container $containerId",
                      changeUrl = routes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = Some(routes.ConfirmRemoveEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url)
                    )
                  )
                )
            }
          }
        }
      }

      "when user answers populated with an in progress equipment" - {

        "and add transport equipment yes/no is unanswered" - {
          "and equipment has no container id" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = "Transport equipment 1",
                      changeUrl = routes.ContainerIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }

          "and equipment has a container id" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, containerId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = s"Transport equipment 1 - container $containerId",
                      changeUrl =
                        seal.routes.SealIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, sealIndex).url,
                      removeUrl = None
                    )
                  )
                )
            }
          }
        }

        "and add transport equipment yes/no is answered" - {
          "and equipment has no container id" in {
            forAll(arbitrary[Mode]) {
              mode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.TransferredToAnotherTransport)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = "Transport equipment 1",
                      changeUrl = routes.ContainerIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = Some(routes.ConfirmRemoveEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url)
                    )
                  )
                )
            }
          }

          "and equipment has a container id" in {
            forAll(arbitrary[Mode], Gen.alphaNumStr) {
              (mode, containerId) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), IncidentCode.TransferredToAnotherTransport)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
                  .setValue(AddTransportEquipmentPage(incidentIndex), true)
                  .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

                val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = s"Transport equipment 1 - container $containerId",
                      changeUrl = routes.AddSealsYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url,
                      removeUrl = Some(routes.ConfirmRemoveEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url)
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
