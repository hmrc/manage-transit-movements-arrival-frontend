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

package viewModels.identification

import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import utils.identification.CheckAuthorisationAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class CheckAuthorisationAnswersViewModel(section: Section)

object CheckAuthorisationAnswersViewModel {

  def apply(userAnswers: UserAnswers, index: Index, mode: Mode)(implicit messages: Messages): CheckAuthorisationAnswersViewModel =
    new CheckAuthorisationAnswersViewModelProvider().apply(userAnswers, index, mode)

  class CheckAuthorisationAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, index: Index, mode: Mode)(implicit messages: Messages): CheckAuthorisationAnswersViewModel = {

      val helper = new CheckAuthorisationAnswersHelper(userAnswers, mode, index)

      val section = Section(
        rows = Seq(
          helper.authorisationType,
          helper.authorisationReferenceNumber
        ).flatten
      )

      new CheckAuthorisationAnswersViewModel(section)
    }
  }
}
