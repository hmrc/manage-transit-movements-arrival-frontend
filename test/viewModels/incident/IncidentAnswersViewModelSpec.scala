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
import viewModels.incident.IncidentAnswersViewModel.IncidentAnswersViewModelProvider

class IncidentAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "must return sections" in {

    forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex), arbitrary[Mode]) {
      (userAnswers, mode) =>
        val sections = new IncidentAnswersViewModelProvider().apply(userAnswers, incidentIndex, mode).sections
        sections.size mustBe 4

        sections.head.sectionTitle must not be defined

        sections(1).sectionTitle.get mustBe "Endorsements"

        sections(2).sectionTitle.get mustBe "Transport equipment"
        val addOrRemoveEquipmentsLink = sections(2).addAnotherLink.value
        addOrRemoveEquipmentsLink.text mustBe "Add or remove transport equipment"
        addOrRemoveEquipmentsLink.id mustBe "add-or-remove-transport-equipment"
        addOrRemoveEquipmentsLink.href mustBe
          controllers.incident.equipment.routes.AddAnotherEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url

        sections(3).sectionTitle.get mustBe "Replacement means of transport"
    }
  }
}
