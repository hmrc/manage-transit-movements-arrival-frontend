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

package viewModels.incident

import config.FrontendAppConfig
import models.{Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.incident.IncidentsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherIncidentViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val numberOfIncidents: Int   = listItems.length
  val singularOrPlural: String = if (numberOfIncidents == 1) "singular" else "plural"

  val prefix: String = "incident.addAnotherIncident"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", numberOfIncidents)
  def heading(implicit messages: Messages): String       = messages.apply(s"$prefix.$singularOrPlural.title", numberOfIncidents)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreIncidents(implicit config: FrontendAppConfig): Boolean = numberOfIncidents < config.maxIncidents
}

object AddAnotherIncidentViewModel {

  class AddAnotherIncidentViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode)(implicit messages: Messages): AddAnotherIncidentViewModel = {
      val helper = new IncidentsAnswersHelper(userAnswers, mode)

      val listItems = helper.listItems.collect {
        case Right(value) => value
        case Left(value)  => value
      }

      new AddAnotherIncidentViewModel(
        listItems,
        onSubmitCall = controllers.incident.routes.AddAnotherIncidentController.onSubmit(userAnswers.mrn, mode)
      )
    }
  }
}
