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

package viewModels.incident

import base.SpecBase
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.incident.AddAnotherIncidentViewModel.AddAnotherIncidentViewModelProvider

class AddAnotherIncidentViewModelSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  "must get list items" - {

    "when there is one incident" in {
      forAll(arbitrary[Mode]) {
        mode =>
          val userAnswers = arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex).sample.value

          val result = new AddAnotherIncidentViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe 1
          result.title mustBe "You have added 1 incident"
          result.heading mustBe "You have added 1 incident"
          result.legend mustBe "Do you want to add another incident?"
          result.maxLimitLabel mustBe "You cannot add any more incidents. To add another, you need to remove one first."
      }
    }

    "when there are multiple incidents" in {
      val formatter = java.text.NumberFormat.getIntegerInstance

      forAll(arbitrary[Mode], Gen.choose(2, frontendAppConfig.maxIncidents)) {
        (mode, numberOfIncidents) =>
          val userAnswers = (0 until numberOfIncidents).foldLeft(emptyUserAnswers) {
            (acc, i) =>
              arbitraryIncidentAnswers(acc, Index(i)).sample.value
          }

          val result = new AddAnotherIncidentViewModelProvider()(userAnswers, mode)
          result.listItems.length mustBe numberOfIncidents
          result.title mustBe s"You have added ${formatter.format(numberOfIncidents)} incidents"
          result.heading mustBe s"You have added ${formatter.format(numberOfIncidents)} incidents"
          result.legend mustBe "Do you want to add another incident?"
          result.maxLimitLabel mustBe "You cannot add any more incidents. To add another, you need to remove one first."
      }
    }
  }

}
