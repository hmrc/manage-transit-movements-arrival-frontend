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

import controllers.identification.authorisation.{routes => authorisationRoutes}
import models.identification.authorisation.AuthorisationType
import models.{Index, Mode, UserAnswers}
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.AnswersHelper

class CheckAuthorisationAnswersHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def authorisationReferenceNumber(eventIndex: Index): Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AuthorisationReferenceNumberPage(eventIndex),
    formatAnswer = formatAsLiteral,
    prefix = "identification.authorisation.authorisationReferenceNumber",
    id = Some(s"change-authorisation-ref-no-${eventIndex.display}"),
    call = authorisationRoutes.AuthorisationReferenceNumberController.onPageLoad(mrn, eventIndex, mode)
  )

  def authorisationType(eventIndex: Index): Option[SummaryListRow] = getAnswerAndBuildRow[AuthorisationType](
    page = AuthorisationTypePage(eventIndex),
    formatAnswer = formatAsLiteral,
    prefix = "identification.authorisation.authorisationType",
    id = Some(s"change-authorisation-type-${eventIndex.display}"),
    call = authorisationRoutes.AuthorisationTypeController.onPageLoad(mrn, eventIndex, mode)
  )

}
