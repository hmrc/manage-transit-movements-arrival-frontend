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

package controllers

import com.google.inject.Inject
import config.PhaseConfig
import connectors.SubmissionConnector
import controllers.actions.Actions
import logging.Logging
import models.MovementReferenceNumber
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.http.HttpReads.{is2xx, is4xx}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ArrivalAnswersViewModel.ArrivalAnswersViewModelProvider
import views.html.CheckArrivalsAnswersView

import scala.concurrent.ExecutionContext

class CheckArrivalsAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckArrivalsAnswersView,
  viewModelProvider: ArrivalAnswersViewModelProvider,
  submissionConnector: SubmissionConnector
)(implicit ec: ExecutionContext, phaseConfig: PhaseConfig)
    extends FrontendBaseController
    with Logging
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val sections = viewModelProvider(request.userAnswers).sections
      Ok(view(mrn, sections))
  }

  def onSubmit(mrn: MovementReferenceNumber): Action[AnyContent] = actions
    .requireData(mrn)
    .async {
      implicit request =>
        submissionConnector.post(mrn.toString).map {
          case response if is2xx(response.status) =>
            logger.debug(s"CheckArrivalsAnswersController:onSubmit: success ${response.status}: ${response.body}")
            Redirect(controllers.routes.DeclarationSubmittedController.onPageLoad(mrn))
          case response if is4xx(response.status) =>
            logger.warn(s"CheckArrivalsAnswersController:onSubmit: bad request: ${response.status}: ${response.body}")
            BadRequest(response.body)
          case e =>
            logger.warn(s"CheckArrivalsAnswersController:onSubmit: something went wrong: ${e.status}-${e.body}")
            InternalServerError(e.body)
        }
    }

}
