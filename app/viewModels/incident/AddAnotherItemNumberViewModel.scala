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
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.incident.ItemsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherItemNumberViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val numberOfItemNumbers: Int = listItems.length
  val singularOrPlural: String = if (numberOfItemNumbers == 1) "singular" else "plural"

  val prefix = "incident.equipment.itemNumber.addAnotherItemNumberYesNo"

  def title(implicit messages: Messages): String   = messages(s"$prefix.$singularOrPlural.title", numberOfItemNumbers)
  def heading(implicit messages: Messages): String = messages(s"$prefix.$singularOrPlural.heading", numberOfItemNumbers)
  def legend(implicit messages: Messages): String  = messages(s"$prefix.label")

  def allowMoreItems(implicit config: FrontendAppConfig): Boolean = numberOfItemNumbers < config.maxNumberOfItems
}

object AddAnotherItemNumberViewModel {

  class AddAnotherItemNumberViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index, equipmentIndex: Index)(implicit messages: Messages): AddAnotherItemNumberViewModel = {
      val helper = new ItemsAnswersHelper(userAnswers, mode, incidentIndex, equipmentIndex)

      val listItems = helper.listItems.collect {
        case Right(value) => value
      }

      new AddAnotherItemNumberViewModel(
        listItems,
        onSubmitCall =
          controllers.incident.equipment.itemNumber.routes.AddAnotherItemNumberYesNoController.onSubmit(userAnswers.mrn, mode, incidentIndex, equipmentIndex)
      )
    }

  }
}
