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

import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.incident.EquipmentAnswersHelper
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

      val sealsSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.seals.subheading"),
        rows = helper.sealsYesNo.toList ++ helper.seals,
        addAnotherLink = helper.addOrRemoveSeals
      )

      val goodsItemNumbersSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.goodsItemNumbers.subheading"),
        rows = helper.goodsItemNumbersYesNo.toList ++ helper.goodsItemNumbers,
        addAnotherLink = helper.addOrRemoveGoodsItemNumber
      )

      new EquipmentAnswersViewModel(Seq(containerSection, sealsSection, goodsItemNumbersSection))
    }
  }
}
