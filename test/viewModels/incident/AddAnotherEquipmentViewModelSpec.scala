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
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.incident.AddAnotherEquipmentViewModel.AddAnotherEquipmentViewModelProvider

class AddAnotherEquipmentViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks with ArrivalUserAnswersGenerator {

  "must get list items" - {

    "when there is one transport equipment" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryEquipmentAnswers(emptyUserAnswers, incidentIndex, Index(0)).sample.value

          val result = new AddAnotherEquipmentViewModelProvider()(userAnswers, mode, incidentIndex)
          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 transport equipment"
          result.heading mustBe "You have added 1 transport equipment"
          result.legend mustBe "Do you want to add any other transport equipment?"
          result.maxLimitLabel mustBe "You cannot add any more transport equipment. To add more, you need to remove one first."
      }
    }

    "when there are multiple transport equipments" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxTransportEquipments)) {
        (mode, numberOfTransportEquipments) =>
          val userAnswers = (0 until numberOfTransportEquipments).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryEquipmentAnswers(acc, incidentIndex, Index(i)).sample.value
          }

          val result = new AddAnotherEquipmentViewModelProvider()(userAnswers, mode, incidentIndex)
          result.listItems.length mustBe numberOfTransportEquipments
          result.title mustBe s"You have added ${formatter.format(numberOfTransportEquipments)} transport equipment"
          result.heading mustBe s"You have added ${formatter.format(numberOfTransportEquipments)} transport equipment"
          result.legend mustBe "Do you want to add any other transport equipment?"
          result.maxLimitLabel mustBe "You cannot add any more transport equipment. To add more, you need to remove one first."
      }
    }
  }

}
