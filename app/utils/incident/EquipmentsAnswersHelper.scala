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

import models.journeyDomain.incident.equipment.EquipmentDomain
import models.journeyDomain.incident.seal.SealDomain
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.Aliases.SummaryListRow
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

case class EquipmentsAnswersHelper(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def equipment(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[EquipmentDomain](
    formatAnswer = formatAsText,
    prefix = "incident.equipment",
    id = Some(s"change-equipment-${index.display}"),
    args = index.display
  )(EquipmentDomain.userAnswersReader(incidentIndex, index))

}
