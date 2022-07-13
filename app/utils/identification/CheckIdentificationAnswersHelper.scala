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

import models.{Mode, UserAnswers}
import pages.identification._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

import java.time.LocalDate

class CheckIdentificationAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers, mode) {

  def arrivalDate(): Option[SummaryListRow] = getAnswerAndBuildRow[LocalDate](
    page = ArrivalDatePage,
    formatAnswer = formatAsDate,
    prefix = "identification.arrivalDate",
    id = Some(s"change-arrival-date")
  )

  def isSimplified(): Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = IsSimplifiedProcedurePage,
    formatAnswer = formatAsYesOrNo,
    prefix = "identification.isSimplifiedProcedure",
    id = Some(s"change-is-simplified-procedure")
  )

  def identificationNumber(): Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "identification.identificationNumber",
    id = Some(s"change-identification-number")
  )
}
