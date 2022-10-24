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

import config.FrontendAppConfig
import controllers.identification.authorisation.{routes => authorisationRoutes}
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.identification.AuthorisationDomain
import models.{Index, Mode, UserAnswers}
import pages.identification.authorisation.AuthorisationTypePage
import pages.sections.AuthorisationsSection
import play.api.i18n.Messages
import play.api.libs.json.Reads
import utils.AnswersHelper
import viewModels.ListItem

class AddAuthorisationHelper(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def listItems: Seq[Either[ListItem, ListItem]] =
    buildListItems(AuthorisationsSection) {
      position =>
        val index = Index(position)
        buildListItem[AuthorisationDomain, AuthorisationType](
          page = AuthorisationTypePage(Index(position)),
          formatJourneyDomainModel = _.toString,
          formatType = _.toString,
          removeRoute = Some(authorisationRoutes.ConfirmRemoveAuthorisationController.onPageLoad(mrn, index))
        )(AuthorisationDomain.userAnswersReader(index), implicitly[Reads[AuthorisationType]])
    }
}
