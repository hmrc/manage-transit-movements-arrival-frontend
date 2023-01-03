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

import config.FrontendAppConfig
import controllers.identification.authorisation.routes
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.identification.AuthorisationsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherAuthorisationViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val numberOfAuthorisations: Int = listItems.length
  val singularOrPlural: String    = if (numberOfAuthorisations == 1) "singular" else "plural"

  val prefix: String = "identification.authorisation.addAnotherAuthorisation"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", numberOfAuthorisations)
  def heading(implicit messages: Messages): String       = messages.apply(s"$prefix.$singularOrPlural.title", numberOfAuthorisations)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreAuthorisations(implicit config: FrontendAppConfig): Boolean = numberOfAuthorisations < config.maxIdentificationAuthorisations
}

object AddAnotherAuthorisationViewModel {

  class AddAnotherAuthorisationViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherAuthorisationViewModel = {
      val helper = new AuthorisationsAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Left(value)  => value
        case Right(value) => value
      }

      new AddAnotherAuthorisationViewModel(
        listItems,
        routes.AddAnotherAuthorisationController.onSubmit(userAnswers.mrn, mode)
      )
    }
  }
}
