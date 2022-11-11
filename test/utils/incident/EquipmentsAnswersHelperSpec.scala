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
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.Mode
import models.incident.IncidentCode
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.incident.equipment.EquipmentDomain
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.IncidentCodePage
import pages.incident.equipment.ContainerIdentificationNumberYesNoPage

class EquipmentsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "EquipmentsAnswersHelper" - {

    "equipment" - {
      "must return None" - {
        "when equipment is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = EquipmentsAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.equipment(equipmentIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when equipment is  defined and container id is undefined" in {
          val initialUserAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
            .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

          forAll(arbitraryEquipmentAnswers(initialUserAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
              val result = helper.equipment(index).get

              result.key.value mustBe "Equipment 1"
              result.value.value mustBe "default container id"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe "#" // TODO - Update when CheckEquipmentAnswersController is built
              action.visuallyHiddenText.get mustBe "equipment 1"
              action.id mustBe "change-equipment-1"
          }
        }

        "when equipment is  defined and container id is defined" in {
          val initialUserAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode.SealsBrokenOrTampered)
            .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

          forAll(arbitraryEquipmentAnswers(initialUserAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val equipment = UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers).value

              val helper = EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)
              val result = helper.equipment(index).get

              result.key.value mustBe "Equipment 1"
              result.value.value mustBe equipment.containerId.get
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe "#" // TODO - Update when CheckEquipmentAnswersController is built
              action.visuallyHiddenText.get mustBe "equipment 1"
              action.id mustBe "change-equipment-1"
          }
        }
      }
    }
  }

}
