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

package utils.incident

import base.SpecBase
import controllers.incident.equipment.itemNumber.routes
import generators.Generators
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident.equipment.AddGoodsItemNumberYesNoPage
import pages.incident.equipment.itemNumber.ItemNumberPage
import viewModels.ListItem

class ItemNumberAnswersHelperSpec extends SpecBase with Generators {

  "ItemNumberAnswersHelper" - {

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = ItemsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with complete item numbers" - {
        "and add item number yes/no page is defined" - {
          "must return list items with remove links" in {
            forAll(arbitrary[Mode], Gen.numStr) {
              (mode, itemNumber) =>
                val userAnswers = emptyUserAnswers
                  .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), true)
                  .setValue(ItemNumberPage(incidentIndex, equipmentIndex, Index(0)), itemNumber)
                  .setValue(ItemNumberPage(incidentIndex, equipmentIndex, Index(1)), itemNumber)

                val helper = ItemsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
                helper.listItems mustBe Seq(
                  Right(
                    ListItem(
                      name = itemNumber,
                      changeUrl = routes.ItemNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0)).url,
                      removeUrl = Some(
                        routes.ConfirmRemoveItemNumberController
                          .onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(0))
                          .url
                      )
                    )
                  ),
                  Right(
                    ListItem(
                      name = itemNumber,
                      changeUrl = routes.ItemNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(1)).url,
                      removeUrl = Some(
                        routes.ConfirmRemoveItemNumberController
                          .onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex, Index(1))
                          .url
                      )
                    )
                  )
                )
            }
          }
        }
      }
    }
  }

}
