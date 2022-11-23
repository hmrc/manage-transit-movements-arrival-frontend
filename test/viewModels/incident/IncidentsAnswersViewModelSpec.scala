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
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.incident.IncidentsAnswersViewModel.IncidentsAnswersViewModelProvider

class IncidentsAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "must return section" in {

    forAll(arbitraryArrivalAnswers(emptyUserAnswers), arbitrary[Mode]) {
      (userAnswers, mode) =>
        val section = new IncidentsAnswersViewModelProvider().apply(userAnswers, mode).section

        section.sectionTitle.get mustBe "Incidents"

        section.rows.head.key.value mustBe "Were there any incidents during the transit?"

        val addOrRemoveIncidentsLink = section.addAnotherLink.value
        addOrRemoveIncidentsLink.text mustBe "Add or remove incidents"
        addOrRemoveIncidentsLink.id mustBe "add-or-remove-incidents"
        addOrRemoveIncidentsLink.href mustBe
          controllers.incident.routes.AddAnotherIncidentController.onPageLoad(userAnswers.mrn, mode).url
    }
  }
}
