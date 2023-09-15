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
import utils.incident.IncidentAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class IncidentAnswersViewModel(sections: Seq[Section])

object IncidentAnswersViewModel {

  class IncidentAnswersViewModelProvider @Inject() () {

    // scalastyle:off method.length
    def apply(userAnswers: UserAnswers, incidentIndex: Index, mode: Mode)(implicit messages: Messages): IncidentAnswersViewModel = {

      val helper = IncidentAnswersHelper(userAnswers, mode, incidentIndex)

      val incidentSection = Section(
        rows = Seq(
          helper.country,
          helper.incidentCode,
          helper.text,
          helper.qualifierOfIdentification,
          helper.unLocode,
          helper.coordinates,
          helper.address,
          helper.containerIndicatorYesNo
        ).flatten
      )

      val endorsementSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.endorsement.subheading"),
        rows = Seq(
          helper.endorsementYesNo,
          helper.endorsementDate,
          helper.endorsementAuthority,
          helper.endorsementCountry,
          helper.endorsementLocation
        ).flatten
      )

      val equipmentsSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.equipments.subheading"),
        rows = helper.transportEquipmentYesNo.toList ++ helper.equipments,
        addAnotherLink = helper.addOrRemoveEquipments
      )

      val transportMeansSection = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.transportMeans.subheading"),
        rows = Seq(
          helper.transportMeansIdentificationType,
          helper.transportMeansIdentificationNumber,
          helper.transportMeansRegisteredCountry
        ).flatten
      )

      new IncidentAnswersViewModel(Seq(incidentSection, endorsementSection, equipmentsSection, transportMeansSection))
    }
    // scalastyle:on method.length
  }
}
