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

package controllers.identification.authorisation

import config.FrontendAppConfig
import controllers.actions._
import forms.AddAnotherItemFormProvider
import models.journeyDomain.identification.AuthorisationDomain
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{ArrivalNavigatorProvider, UserAnswersNavigator}
import pages.sections.identification.{AuthorisationSection, AuthorisationsSection}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.identification.AddAnotherAuthorisationViewModel
import viewModels.identification.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider
import views.html.identification.authorisation.AddAnotherAuthorisationView

import javax.inject.Inject

class AddAnotherAuthorisationController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: ArrivalNavigatorProvider,
  actions: Actions,
  removeInProgressAuthorisations: RemoveInProgressActionProvider,
  formProvider: AddAnotherItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  viewModelProvider: AddAnotherAuthorisationViewModelProvider,
  view: AddAnotherAuthorisationView
)(implicit config: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  private def form(viewModel: AddAnotherAuthorisationViewModel): Form[Boolean] =
    formProvider(viewModel.prefix, viewModel.allowMoreAuthorisations)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(removeInProgressAuthorisations[AuthorisationDomain](AuthorisationsSection, AuthorisationSection)) {
      implicit request =>
        val viewModel = viewModelProvider(request.userAnswers, mode)
        viewModel.numberOfAuthorisations match {
          case 0 => Redirect(controllers.identification.routes.IsSimplifiedProcedureController.onPageLoad(mrn, mode))
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
            case true =>
              Redirect(
                UserAnswersNavigator
                  .nextPage[AuthorisationDomain](request.userAnswers, mode)(AuthorisationDomain.userAnswersReader(Index(viewModel.numberOfAuthorisations)))
              )
            case false =>
              Redirect(navigatorProvider(mode).nextPage(request.userAnswers))
          }
        )
  }
}
