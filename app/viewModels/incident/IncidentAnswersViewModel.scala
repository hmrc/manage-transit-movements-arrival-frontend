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

import models.{Index, Mode, RichOptionJsArray, UserAnswers}
import pages.sections.incident.EquipmentsSection
import play.api.i18n.Messages
import utils.incident.IncidentAnswersHelper
import viewModels.Link
import viewModels.sections.Section

import javax.inject.Inject

case class IncidentAnswersViewModel(sections: Seq[Section])

object IncidentAnswersViewModel {

  class IncidentAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, incidentIndex: Index, mode: Mode)(implicit messages: Messages): IncidentAnswersViewModel = {

      val helper = IncidentAnswersHelper(userAnswers, mode, incidentIndex)

      val incidentSection = Section(
        rows = Seq(
          helper.country,
          helper.code,
          helper.text,
          helper.text,
          helper.endorsementYesNo,
          helper.endorsementDate,
          helper.endorsementAuthority,
          helper.endorsementCountry,
          helper.endorsementLocation,
          helper.qualifierOfIdentification,
          helper.unLocode,
          helper.coordinates,
          helper.address,
          helper.containerIndicatorYesNo,
          helper.transportEquipmentYesNo
        ).flatten
      )

      val equipmentsSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.equipments.subheading"),
        rows = userAnswers
          .get(EquipmentsSection(incidentIndex))
          .mapWithIndex {
            (_, index) => helper.equipment(Index(index))
          },
        addAnotherLink = Link(
          id = "add-or-remove-equipments",
          text = messages("arrivals.checkYourAnswers.equipments.addOrRemove"),
          href = "#" // TODO - add another equipment page
        )
      )

      // TODO - add section for transport means questions

      new IncidentAnswersViewModel(Seq(incidentSection, equipmentsSection))
    }
  }
}
