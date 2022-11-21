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

  "must get list items" in {

    forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxTransportEquipments)) {
      (mode, numberOfTransportEquipments) =>
        val userAnswers = (0 until numberOfTransportEquipments).foldLeft(emptyUserAnswers) {
          (acc, i) =>
            arbitraryEquipmentAnswers(acc, incidentIndex, Index(i)).sample.value
        }

        val result = new AddAnotherEquipmentViewModelProvider()(userAnswers, mode, incidentIndex)
        result.listItems.length mustBe numberOfTransportEquipments
    }
  }

}
