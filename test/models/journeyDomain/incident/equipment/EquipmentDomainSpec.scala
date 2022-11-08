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
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.ContainerIndicatorYesNoPage
import pages.incident.equipment._

class EquipmentDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  private val containerId = Gen.alphaNumStr.sample.value

  "EquipmentDomain" - {

    "can be parsed from user answers" - {

      "when container indicator is true" - {
        "and container id is answered" in {
          val userAnswers = emptyUserAnswers
            .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
            .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

          val expectedResult = EquipmentDomain(Some(containerId))(incidentIndex, equipmentIndex)

          val result: EitherType[EquipmentDomain] =
            UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

          result.value mustBe expectedResult
        }
      }

      "when container indicator is false" - {
        "and index is 0" - {
          "and container id is answered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)
              .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

            val expectedResult = EquipmentDomain(Some(containerId))(incidentIndex, equipmentIndex)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.value mustBe expectedResult
          }

          "and container id is unanswered" in {
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

            val expectedResult = EquipmentDomain(None)(incidentIndex, equipmentIndex)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)).run(userAnswers)

            result.value mustBe expectedResult
          }
        }

        "and indicator is not 0" ignore {}
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
      }

      "when container indicator is false" - {
        "and index is 0" - {
          "and add container id number is unanswered" in {
            val userAnswers = emptyUserAnswers.setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

            val result: EitherType[EquipmentDomain] =
              UserAnswersReader[EquipmentDomain](EquipmentDomain.userAnswersReader(incidentIndex, Index(0))).run(userAnswers)

            result.left.value.page mustBe ContainerIdentificationNumberYesNoPage(incidentIndex, Index(0))
          }
        }
      }
    }
  }

}
