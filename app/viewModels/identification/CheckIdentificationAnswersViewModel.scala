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

import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import utils.identification.CheckIdentificationAnswersHelper
import viewModels.sections.Section

import javax.inject.Inject

case class CheckIdentificationAnswersViewModel(sections: Seq[Section])

object CheckIdentificationAnswersViewModel {

  def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): CheckIdentificationAnswersViewModel =
    new CheckIdentificationAnswersViewModelProvider().apply(userAnswers, mode)

  class CheckIdentificationAnswersViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): CheckIdentificationAnswersViewModel = {

      val helper = new CheckIdentificationAnswersHelper(userAnswers, mode)

      // TODO - add rows for each authorisation
      val section = Section(
        rows = Seq(
          helper.arrivalDate,
          helper.isSimplified,
          helper.identificationNumber
        ).flatten
      )

      new CheckIdentificationAnswersViewModel(Seq(section))
    }
  }
}
