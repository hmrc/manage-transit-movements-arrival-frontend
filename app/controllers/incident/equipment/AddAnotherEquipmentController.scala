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

package controllers.incident.equipment

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherItemFormProvider
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.sections.incident.{EquipmentSection, EquipmentsSection}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.incident.AddAnotherEquipmentViewModel
import viewModels.incident.AddAnotherEquipmentViewModel.AddAnotherEquipmentViewModelProvider
import views.html.incident.equipment.AddAnotherEquipmentView

import javax.inject.Inject

class AddAnotherEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  removeInProgressTransportEquipments: RemoveInProgressActionProvider,
  formProvider: AddAnotherItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  viewModelProvider: AddAnotherEquipmentViewModelProvider,
  view: AddAnotherEquipmentView
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherEquipmentViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMoreEquipments)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(
      removeInProgressTransportEquipments[EquipmentDomain](
        EquipmentsSection(incidentIndex),
        EquipmentSection(incidentIndex, _)
      )(EquipmentDomain.userAnswersReader(incidentIndex, _))
    ) {
      implicit request =>
        val viewModel = viewModelProvider(request.userAnswers, mode, incidentIndex)
        viewModel.numberOfTransportEquipments match {
          case 0 => Redirect(controllers.incident.routes.AddTransportEquipmentController.onPageLoad(mrn, mode, incidentIndex))
          case _ => Ok(view(form(viewModel), mrn, viewModel))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      lazy val viewModel = viewModelProvider(request.userAnswers, mode, incidentIndex)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, mrn, viewModel)),
          {
            case true =>
              Redirect(
                UserAnswersNavigator
                  .nextPage[EquipmentDomain](request.userAnswers, mode)(
                    EquipmentDomain.userAnswersReader(incidentIndex, Index(viewModel.numberOfTransportEquipments))
                  )
              )
            case false =>
              Redirect(navigatorProvider(mode, incidentIndex).nextPage(request.userAnswers))
          }
        )
  }
}
