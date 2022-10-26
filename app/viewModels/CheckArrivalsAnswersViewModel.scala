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

package viewModels

import models.{CheckMode, UserAnswers}
import play.api.i18n.Messages
import viewModels.LocationOfGoodsAnswersViewModel.LocationOfGoodsAnswersViewModelProvider
import viewModels.identification.AuthorisationsAnswersViewModel.AuthorisationsAnswersViewModelProvider
import viewModels.identification.IdentificationAnswersViewModel.IdentificationAnswersViewModelProvider
import viewModels.sections.Section

import javax.inject.Inject

case class CheckArrivalsAnswersViewModel(sections: Seq[Section])

object CheckArrivalsAnswersViewModel {

  class CheckArrivalsAnswersViewModelProvider @Inject() (
    identificationAnswersViewModelProvider: IdentificationAnswersViewModelProvider,
    authorisationsAnswersViewModelProvider: AuthorisationsAnswersViewModelProvider,
    locationOfGoodsAnswersViewModelProvider: LocationOfGoodsAnswersViewModelProvider
  ) {

    def apply(userAnswers: UserAnswers)(implicit messages: Messages): CheckArrivalsAnswersViewModel = {
      val mode = CheckMode
      new CheckArrivalsAnswersViewModel(
        identificationAnswersViewModelProvider.apply(userAnswers, mode).section ::
          authorisationsAnswersViewModelProvider.apply(userAnswers, mode).section ::
          locationOfGoodsAnswersViewModelProvider.apply(userAnswers, mode).section ::
          Nil
      )
    }
  }
}
