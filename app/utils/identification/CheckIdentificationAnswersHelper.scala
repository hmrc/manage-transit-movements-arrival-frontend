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

package utils.identification

import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.identification.AuthorisationDomain
import models.{Index, Mode, UserAnswers}
import pages.identification._
import pages.identification.authorisation.AuthorisationTypePage
import play.api.i18n.Messages
import play.api.libs.json.Reads
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

import java.time.LocalDate

class CheckIdentificationAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def movementReferenceNumber: SummaryListRow = buildRow(
    prefix = "movementReferenceNumber",
    answer = formatAsText(mrn),
    id = None,
    call = controllers.identification.routes.MovementReferenceNumberController.onPageLoad()
  )

  def arrivalDate: Option[SummaryListRow] = getAnswerAndBuildRow[LocalDate](
    page = ArrivalDatePage,
    formatAnswer = formatAsDate,
    prefix = "identification.arrivalDate",
    id = Some("change-arrival-date")
  )

  def isSimplified: Option[SummaryListRow] = getAnswerAndBuildRow[ProcedureType](
    page = IsSimplifiedProcedurePage,
    formatAnswer = formatEnumAsText(ProcedureType.messageKeyPrefix),
    prefix = "identification.isSimplifiedProcedure",
    id = Some("change-is-simplified-procedure")
  )

  def identificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "identification.identificationNumber",
    id = Some("change-identification-number")
  )

  def authorisation(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[AuthorisationDomain](
    formatAnswer = formatAsText,
    prefix = "identification.authorisation",
    id = Some(s"change-authorisation-${index.display}"),
    args = index.display
  )(AuthorisationDomain.userAnswersReader(index))

}
