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

package controllers.incident.equipment.itemNumber

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherItemFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.EquipmentNavigatorProvider
import pages.sections.incident.ItemsSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.incident.AddAnotherItemNumberViewModel
import viewModels.incident.AddAnotherItemNumberViewModel.AddAnotherItemNumberViewModelProvider
import views.html.incident.equipment.itemNumber.AddAnotherItemNumberYesNoView

import javax.inject.Inject

class AddAnotherItemNumberYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  navigatorProvider: EquipmentNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  viewModelProvider: AddAnotherItemNumberViewModelProvider,
  view: AddAnotherItemNumberYesNoView
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherItemNumberViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMoreItems)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions
    .requireData(mrn) {
      implicit request =>
        val viewModel = viewModelProvider(request.userAnswers, mode, incidentIndex, equipmentIndex)
        Ok(view(form(viewModel), mrn, viewModel))
        viewModel.numberOfItemNumbers match {
          case 0 => Redirect(controllers.incident.equipment.routes.AddGoodsItemNumberYesNoController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex))
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
              Redirect(
                controllers.incident.equipment.itemNumber.routes.ItemNumberController
                  .onPageLoad(mrn, mode, incidentIndex, equipmentIndex, Index(viewModel.numberOfItemNumbers))
              )
            case false =>
              Redirect(navigatorProvider(mode, incidentIndex, equipmentIndex).nextPage(request.userAnswers, Some(ItemsSection(incidentIndex, equipmentIndex))))
          }
        )
  }
}
