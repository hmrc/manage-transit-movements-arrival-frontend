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
import pages.incident.equipment.ContainerIdentificationNumberPage
import pages.incident.equipment.seal.SealIdentificationNumberPage
import viewModels.incident.AddAnotherSealViewModel.AddAnotherSealViewModelProvider

class AddAnotherSealViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" in {

    forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxSeals)) {
      (mode, numberOfSeals) =>
        val userAnswers = (0 until numberOfSeals).foldLeft(emptyUserAnswers) {
          (acc, i) =>
            acc.setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(i)), Gen.alphaNumStr.sample.value)
        }

        val result = new AddAnotherSealViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
        result.listItems.length mustBe numberOfSeals
    }
  }

  "when container id is undefined" - {
    "prefix must be incident.equipment.seal.addAnotherSeal.withoutContainer" in {
      forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxSeals)) {
        (mode, numberOfSeals) =>
          val userAnswers = (0 until numberOfSeals).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              acc.setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(i)), Gen.alphaNumStr.sample.value)
          }

          val result = new AddAnotherSealViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
          result.prefix mustBe "incident.equipment.seal.addAnotherSeal.withoutContainer"
          result.args mustBe Seq(numberOfSeals)
      }
    }
  }

  "when container id is defined" - {
    "prefix must be incident.equipment.seal.addAnotherSeal.withContainer" in {
      val containerId    = Gen.alphaNumStr.sample.value
      val initialAnswers = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)

      forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxSeals)) {
        (mode, numberOfSeals) =>
          val userAnswers = (0 until numberOfSeals).foldLeft(initialAnswers) {
            (acc, i) =>
              acc.setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(i)), Gen.alphaNumStr.sample.value)
          }

          val result = new AddAnotherSealViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
          result.prefix mustBe "incident.equipment.seal.addAnotherSeal.withContainer"
          result.args mustBe Seq(numberOfSeals, containerId)
      }
    }
  }

}