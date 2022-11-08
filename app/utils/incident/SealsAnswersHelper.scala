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

package utils.incident

import models.journeyDomain.incident.seal.SealDomain
import models.{Index, Mode, UserAnswers}
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.sections.incident.SealsSection
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.AnswersHelper
import viewModels.ListItem

class SealsAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index,
  equipmentIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(SealsSection(incidentIndex, equipmentIndex)) {
      position =>
        val index = Index(position)
        buildListItem[SealDomain, String](
          page = SealIdentificationNumberPage(incidentIndex, equipmentIndex, Index(position)),
          formatJourneyDomainModel = _.identificationNumber,
          formatType = identity,
          removeRoute = Some(Call("GET", "#")) // TODO - create remove page
        )(SealDomain.userAnswersReader(incidentIndex, equipmentIndex, index), implicitly)
    }
}

object SealsAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index, equipmentIndex: Index)(implicit messages: Messages) =
    new SealsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)
}
