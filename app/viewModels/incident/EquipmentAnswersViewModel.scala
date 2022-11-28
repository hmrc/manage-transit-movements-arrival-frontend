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

import controllers.incident.equipment._
import models.{Index, Mode, RichOptionJsArray, UserAnswers}
import pages.sections.incident.{ItemsSection, SealsSection}
import play.api.i18n.Messages
import utils.incident.EquipmentAnswersHelper
import viewModels.Link
import viewModels.sections.Section

import javax.inject.Inject

case class EquipmentAnswersViewModel(sections: Seq[Section])

object EquipmentAnswersViewModel {

  class EquipmentAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, incidentIndex: Index, equipmentIndex: Index, mode: Mode)(implicit messages: Messages): EquipmentAnswersViewModel = {

      val helper = EquipmentAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)

      val containerSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.container.subheading"),
        rows = Seq(
          helper.containerIdentificationNumberYesNo,
          helper.containerIdentificationNumber
        ).flatten
      )

      val sealsSection = {
        val sealRows = userAnswers
          .get(SealsSection(incidentIndex, equipmentIndex))
          .mapWithIndex {
            (_, index) => helper.seal(index)
          }

        Section(
          sectionTitle = messages("arrivals.checkYourAnswers.seals.subheading"),
          rows = helper.sealsYesNo.toList ++ sealRows,
          addAnotherLink = Link(
            id = "add-or-remove-seals",
            text = messages("arrivals.checkYourAnswers.seals.addOrRemove"),
            href = seal.routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
          )
        )
      }

      val goodsItemNumbersSection = {
        val goodsItemNumberRows = userAnswers
          .get(ItemsSection(incidentIndex, equipmentIndex))
          .mapWithIndex {
            (_, index) => helper.goodsItemNumber(index)
          }

        Section(
          sectionTitle = messages("arrivals.checkYourAnswers.goodsItemNumbers.subheading"),
          rows = helper.goodsItemNumbersYesNo.toList ++ goodsItemNumberRows,
          addAnotherLink = Link(
            id = "add-or-remove-goods-item-numbers",
            text = messages("arrivals.checkYourAnswers.goodsItemNumbers.addOrRemove"),
            href = itemNumber.routes.AddAnotherItemNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
          )
        )
      }

      new EquipmentAnswersViewModel(Seq(containerSection, sealsSection, goodsItemNumbersSection))
    }
  }
}
