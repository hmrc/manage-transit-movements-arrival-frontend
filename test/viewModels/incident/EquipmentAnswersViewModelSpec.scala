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
import controllers.incident.equipment._
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import viewModels.incident.EquipmentAnswersViewModel.EquipmentAnswersViewModelProvider

class EquipmentAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "must return sections" in {

    forAll(arbitraryEquipmentAnswers(emptyUserAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
      (userAnswers, mode) =>
        val sections = new EquipmentAnswersViewModelProvider().apply(userAnswers, incidentIndex, equipmentIndex, mode).sections
        sections.size mustBe 3

        val addOrRemoveSealsLink = sections(1).addAnotherLink.value
        addOrRemoveSealsLink.text mustBe "Add or remove seals"
        addOrRemoveSealsLink.id mustBe "add-or-remove-seals"
        addOrRemoveSealsLink.href mustBe
          seal.routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url

        val addOrRemoveGoodsItemNumbersLink = sections(2).addAnotherLink.value
        addOrRemoveGoodsItemNumbersLink.text mustBe "Add or remove goods item numbers"
        addOrRemoveGoodsItemNumbersLink.id mustBe "add-or-remove-goods-item-numbers"
        addOrRemoveGoodsItemNumbersLink.href mustBe
          itemNumber.routes.AddAnotherItemNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
    }
  }
}
