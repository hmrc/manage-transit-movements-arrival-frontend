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

package utils

import controllers.identification.authorisation.{routes => authorisationRoutes}
import models.{CheckMode, Index, Mode, UserAnswers}
import pages.identification.authorisation.AuthorisationReferenceNumberPage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.html.components.implicits._
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem

class AddAuthorisationsHelper(prefix: String, userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages) extends AnswersHelper(userAnswers) {

  def authorisationListItem(eventIndex: Index): Option[ListItem] =
    authorisationReference(eventIndex).map {
      answer =>
        ListItem(
          name = answer,
          changeUrl = "changeUrl",
          removeUrl = "removeUrl"
        )
    }

  def authorisation(authorisationIndex: Index): Option[SummaryListRow] =
    authorisationReference(authorisationIndex) map {
      answer =>
        buildSectionRow(
          prefix = s"$prefix.authorisation",
          labelKey = s"$prefix.authorisation",
          answer = answer.toText,
          id = None,
          call = authorisationRoutes.AuthorisationReferenceNumberController.onPageLoad(mrn, authorisationIndex, CheckMode), //TO CYA?
          args = authorisationIndex.display
        )
    }

  private def authorisationReference(authorisationIndex: Index): Option[String] =
    userAnswers.get(AuthorisationReferenceNumberPage(authorisationIndex))
}
