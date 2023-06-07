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

import models.identification.authorisation.AuthorisationType
import models.{Index, Mode, UserAnswers}
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

class AuthorisationAnswersHelper(
  userAnswers: UserAnswers,
  mode: Mode,
  index: Index
)(implicit messages: Messages)
    extends AnswersHelper(userAnswers, mode) {

//  def authorisationType: Option[SummaryListRow] = getAnswerAndBuildRow[AuthorisationType](
//    page = AuthorisationTypePage(index),
//    formatAnswer = formatEnumAsText(AuthorisationType.messageKeyPrefix),
//    prefix = "identification.authorisation.authorisationType",
//    id = Some("change-authorisation-type")
//  )

  def authorisationReferenceNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AuthorisationReferenceNumberPage(index),
    formatAnswer = formatAsText,
    prefix = "identification.authorisation.authorisationReferenceNumber",
    id = Some("change-authorisation-reference-number")
  )
}

object AuthorisationAnswersHelper {

  def apply(
    userAnswers: UserAnswers,
    mode: Mode,
    index: Index
  )(implicit messages: Messages) =
    new AuthorisationAnswersHelper(userAnswers, mode, index)
}
