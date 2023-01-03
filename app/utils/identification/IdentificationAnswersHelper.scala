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

package utils.identification

import models.identification.ProcedureType
import models.reference.CustomsOffice
import models.{Mode, UserAnswers}
import pages.identification.{DestinationOfficePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

class IdentificationAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

  def movementReferenceNumber: SummaryListRow = buildRow(
    prefix = "movementReferenceNumber",
    answer = formatAsText(mrn),
    id = None,
    call = controllers.identification.routes.MovementReferenceNumberController.onPageLoad(mode)
  )

  def destinationOffice: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = DestinationOfficePage,
    formatAnswer = formatAsText,
    prefix = "identification.destinationOffice",
    id = Some("change-destination-office")
  )

  def identificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "identification.identificationNumber",
    id = Some("change-identification-number")
  )

  def isSimplified: Option[SummaryListRow] = getAnswerAndBuildRow[ProcedureType](
    page = IsSimplifiedProcedurePage,
    formatAnswer = formatEnumAsText(ProcedureType.messageKeyPrefix),
    prefix = "identification.isSimplifiedProcedure",
    id = Some("change-is-simplified-procedure")
  )

}

object IdentificationAnswersHelper {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode
  )(implicit messages: Messages): IdentificationAnswersHelper =
    new IdentificationAnswersHelper(userAnswers, mode)
}
