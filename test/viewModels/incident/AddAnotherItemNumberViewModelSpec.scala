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
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.itemNumber.ItemNumberPage
import viewModels.incident.AddAnotherItemNumberViewModel.AddAnotherItemNumberViewModelProvider

class AddAnotherItemNumberViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" in {

    forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxNumberOfItems)) {
      (mode, numberOfItems) =>
        val userAnswers = (0 until numberOfItems).foldLeft(emptyUserAnswers) {
          (acc, i) =>
            acc.setValue(ItemNumberPage(incidentIndex, equipmentIndex, Index(i)), Gen.alphaNumStr.sample.value)
        }

        val result = new AddAnotherItemNumberViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
        result.listItems.length mustBe numberOfItems
    }
  }

  "must determine title and heading" - {
    "and there is one item number" in {
      val userAnswers = emptyUserAnswers
        .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), Gen.alphaNumStr.sample.value)

      forAll(arbitrary[Mode]) {
        mode =>
          val result = new AddAnotherItemNumberViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
          result.prefix mustBe "incident.equipment.itemNumber.addAnotherItemNumberYesNo"
          result.title mustBe "You have added 1 goods item number"
          result.heading mustBe "You have added 1 goods item number"
      }
    }

    "and there are multiple item numbers" in {
      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxNumberOfItems)) {
        (mode, numberOfItems) =>
          val userAnswers = (0 until numberOfItems).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              acc.setValue(ItemNumberPage(incidentIndex, equipmentIndex, Index(i)), Gen.alphaNumStr.sample.value)
          }

          val result = new AddAnotherItemNumberViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
          result.prefix mustBe "incident.equipment.itemNumber.addAnotherItemNumberYesNo"
          result.title mustBe s"You have added $numberOfItems goods item numbers"
          result.heading mustBe s"You have added $numberOfItems goods item numbers"
      }
    }

  }

}
