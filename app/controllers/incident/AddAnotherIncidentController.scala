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

package controllers.incident

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherItemFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.ArrivalNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpVerbs.GET
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.incident.AddAnotherIncidentViewModel
import viewModels.incident.AddAnotherIncidentViewModel.AddAnotherIncidentViewModelProvider
import views.html.incident.AddAnotherIncidentView

import javax.inject.Inject

class AddAnotherIncidentController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: ArrivalNavigatorProvider,
  actions: Actions,
  formProvider: AddAnotherItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  viewModelProvider: AddAnotherIncidentViewModelProvider,
  view: AddAnotherIncidentView
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherIncidentViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMoreIncidents)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn) {
      implicit request =>
        val viewModel = viewModelProvider(request.userAnswers, mode)
        viewModel.numberOfIncidents match {
          case 0 => Redirect(controllers.incident.routes.IncidentFlagController.onPageLoad(mrn, mode))
          case _ => Ok(view(form(viewModel), mrn, viewModel))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      lazy val viewModel = viewModelProvider(request.userAnswers, mode)
      form(viewModel)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, mrn, viewModel)),
          {
            case true  => Redirect(Call(GET, "#")) // TODO - incident CYA
            case false => Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
          }
        )
  }
}
