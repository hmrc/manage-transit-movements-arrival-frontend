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

import config.{FrontendAppConfig, PhaseConfig}
import controllers.incident.equipment.routes
import models.{Index, Mode, UserAnswers}
import play.api.i18n.Messages
import play.api.mvc.Call
import utils.incident.EquipmentsAnswersHelper
import viewModels.ListItem

import javax.inject.Inject

case class AddAnotherEquipmentViewModel(
  listItems: Seq[ListItem],
  onSubmitCall: Call
) {

  val numberOfTransportEquipments: Int = listItems.length
  val singularOrPlural: String         = if (numberOfTransportEquipments == 1) "singular" else "plural"

  val prefix: String = "incident.equipment.addAnotherTransportEquipment"

  def title(implicit messages: Messages): String         = messages(s"$prefix.$singularOrPlural.title", numberOfTransportEquipments)
  def heading(implicit messages: Messages): String       = messages.apply(s"$prefix.$singularOrPlural.title", numberOfTransportEquipments)
  def legend(implicit messages: Messages): String        = messages(s"$prefix.label")
  def maxLimitLabel(implicit messages: Messages): String = messages(s"$prefix.maxLimit.label")

  def allowMoreEquipments(implicit config: FrontendAppConfig): Boolean = numberOfTransportEquipments < config.maxTransportEquipments
}

object AddAnotherEquipmentViewModel {

  class AddAnotherEquipmentViewModelProvider @Inject() () {

    def apply(userAnswers: UserAnswers, mode: Mode, incidentIndex: Index)(implicit
      messages: Messages,
      phaseConfig: PhaseConfig
    ): AddAnotherEquipmentViewModel = {
      val helper = new EquipmentsAnswersHelper(userAnswers, mode, incidentIndex)

      val listItems = helper.listItems.collect {
        case Right(value) => value
        case Left(value)  => value
      }

      new AddAnotherEquipmentViewModel(
        listItems,
        onSubmitCall = routes.AddAnotherEquipmentController.onSubmit(userAnswers.mrn, mode, incidentIndex)
      )
    }
  }
}
