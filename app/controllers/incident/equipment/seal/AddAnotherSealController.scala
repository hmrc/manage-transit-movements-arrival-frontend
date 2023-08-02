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

package controllers.incident.equipment.seal

import config.{FrontendAppConfig, PhaseConfig}
import controllers.actions._
import forms.AddAnotherItemFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.EquipmentNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.incident.AddAnotherSealViewModel
import viewModels.incident.AddAnotherSealViewModel.AddAnotherSealViewModelProvider
import views.html.incident.equipment.seal.AddAnotherSealView

import javax.inject.Inject

class AddAnotherSealController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: EquipmentNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  viewModelProvider: AddAnotherSealViewModelProvider,
  view: AddAnotherSealView
)(implicit config: FrontendAppConfig, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherSealViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMoreSeals)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions
    .requireData(mrn) {
      implicit request =>
        val viewModel = viewModelProvider(request.userAnswers, mode, incidentIndex, equipmentIndex)
        viewModel.numberOfSeals match {
          case 0 => Redirect(controllers.incident.equipment.routes.AddSealsYesNoController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex))
          case _ => Ok(view(form(viewModel), mrn, viewModel))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      lazy val viewModel = viewModelProvider(request.userAnswers, mode, incidentIndex, equipmentIndex)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, mrn, viewModel)),
          {
            case true =>
              Redirect(routes.SealIdentificationNumberController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex, Index(viewModel.numberOfSeals)))
            case false => Redirect(navigatorProvider(mode, incidentIndex, equipmentIndex).nextPage(request.userAnswers))
          }
        )
  }
}
