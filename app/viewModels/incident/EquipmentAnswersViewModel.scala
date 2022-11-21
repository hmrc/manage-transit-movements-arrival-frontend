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
import pages.sections.incident.SealsSection
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

      val equipmentSection = Section(
        rows = Seq(
          helper.containerIdentificationNumberYesNo,
          helper.containerIdentificationNumber,
          helper.sealsYesNo
        ).flatten
      )

      val sealsSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.seals.subheading"),
        rows = userAnswers
          .get(SealsSection(incidentIndex, equipmentIndex))
          .mapWithIndex {
            (_, index) => helper.seal(Index(index))
          },
        addAnotherLink = Link(
          id = "add-or-remove-seals",
          text = messages("arrivals.checkYourAnswers.seals.addOrRemove"),
          href = seal.routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
        )
      )

      // TODO - add goods references section

      new EquipmentAnswersViewModel(Seq(equipmentSection, sealsSection))
    }
  }
}
