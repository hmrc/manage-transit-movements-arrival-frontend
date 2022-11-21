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

import models.{Index, Mode, UserAnswers}
import pages.incident.equipment.seal.SealIdentificationNumberPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

class SealAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  incidentIndex: Index,
  equipmentIndex: Index,
  sealIndex: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def sealIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
    formatAnswer = formatAsText,
    prefix = "incident.equipment.seal.sealIdentificationNumber",
    id = Some("change-seal-identification-number")
  )

}

object SealAnswersHelper {

  def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index)(implicit messages: Messages) =
    new SealAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex, sealIndex)
}
