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

  "must determine title and heading" - {
    "when container id is undefined" - {
      "and there is one seal" in {
        val userAnswers = emptyUserAnswers
          .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), Gen.alphaNumStr.sample.value)

        forAll(arbitrary[Mode]) {
          mode =>
            val result = new AddAnotherSealViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
            result.prefix mustBe "incident.equipment.seal.addAnotherSeal.withoutContainer"
            result.args mustBe Seq(1)
            result.title mustBe "You have added 1 seal"
            result.heading mustBe "You have added 1 seal"
            result.legend mustBe "Do you want to add another seal?"
        }
      }

      "and there are multiple seals" in {
        forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxSeals)) {
          (mode, numberOfSeals) =>
            val userAnswers = (0 until numberOfSeals).foldLeft(emptyUserAnswers) {
              (acc, i) =>
                acc.setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(i)), Gen.alphaNumStr.sample.value)
            }

            val result = new AddAnotherSealViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
            result.prefix mustBe "incident.equipment.seal.addAnotherSeal.withoutContainer"
            result.args mustBe Seq(numberOfSeals)
            result.title mustBe s"You have added $numberOfSeals seals"
            result.heading mustBe s"You have added $numberOfSeals seals"
            result.legend mustBe "Do you want to add another seal?"
        }
      }
    }

    "when container id is defined" - {

      val containerId = nonEmptyString.sample.value

      "and there is one seal" in {
        val userAnswers = emptyUserAnswers
          .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), containerId)
          .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), Gen.alphaNumStr.sample.value)

        forAll(arbitrary[Mode]) {
          mode =>
            val result = new AddAnotherSealViewModelProvider()(userAnswers, mode, incidentIndex, equipmentIndex)
            result.prefix mustBe "incident.equipment.seal.addAnotherSeal.withContainer"
            result.args mustBe Seq(1, containerId)
            result.title mustBe s"You have added 1 seal for container $containerId"
            result.heading mustBe s"You have added 1 seal for container $containerId"
            result.legend mustBe s"Do you want to add another seal for container $containerId?"
        }
      }

      "and there are multiple seals" in {
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
            result.title mustBe s"You have added $numberOfSeals seals for container $containerId"
            result.heading mustBe s"You have added $numberOfSeals seals for container $containerId"
            result.legend mustBe s"Do you want to add another seal for container $containerId?"
        }
      }
    }
  }

}
