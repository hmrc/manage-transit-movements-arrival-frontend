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

package viewModels.identification

import models.{Mode, RichOptionJsArray, UserAnswers}
import pages.sections.identification.AuthorisationsSection
import play.api.i18n.Messages
import utils.identification.AuthorisationsAnswersHelper
import viewModels.Link
import viewModels.sections.Section

import javax.inject.Inject

case class AuthorisationsAnswersViewModel(section: Section)

object AuthorisationsAnswersViewModel {

  class AuthorisationsAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AuthorisationsAnswersViewModel = {

      val helper = AuthorisationsAnswersHelper(userAnswers, mode)

      val section = Section(
        sectionTitle = messages("arrivals.checkYourAnswers.authorisations.subheading"),
        rows = userAnswers
          .get(AuthorisationsSection)
          .mapWithIndex {
            (_, index) => helper.authorisation(index)
          },
        addAnotherLink = Link(
          id = "add-or-remove-authorisations",
          text = messages("arrivals.checkYourAnswers.authorisations.addOrRemove"),
          href = controllers.identification.authorisation.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.mrn, mode).url
        )
      )

      new AuthorisationsAnswersViewModel(section)
    }
  }
}
