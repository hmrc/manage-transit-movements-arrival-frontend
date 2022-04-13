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

package controllers

import com.google.inject.Inject
import controllers.actions.Actions
import handlers.ErrorHandler
import models.MovementReferenceNumber
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ArrivalSubmissionService
import uk.gov.hmrc.http.HttpErrorFunctions
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.CheckYourAnswersViewModel
import viewModels.sections.Section
import views.html.CheckYourAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CheckYourAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  service: ArrivalSubmissionService,
  errorHandler: ErrorHandler,
  val controllerComponents: MessagesControllerComponents,
  view: CheckYourAnswersView,
  viewModel: CheckYourAnswersViewModel
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with HttpErrorFunctions {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val sections: Seq[Section] = viewModel(request.userAnswers)
      Ok(view(mrn, sections))
  }

  def onSubmit(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.submit(request.userAnswers) flatMap {
        case Some(result) =>
          result.status match {
            case status if is2xx(status) => Future.successful(Redirect(routes.ConfirmationController.onPageLoad(mrn)))
            case status if is4xx(status) => errorHandler.onClientError(request, status)
            case _                       => errorHandler.onClientError(request, INTERNAL_SERVER_ERROR)
          }
        case None => errorHandler.onClientError(request, INTERNAL_SERVER_ERROR)
      }
  }
}
