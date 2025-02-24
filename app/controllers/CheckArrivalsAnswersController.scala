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
import controllers.actions.Actions
import models.journeyDomain.ArrivalDomain
import models.{MovementReferenceNumber, NormalMode}
import navigation.UserAnswersNavigator
import play.api.Logging
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SubmissionService
import uk.gov.hmrc.http.HttpErrorFunctions.is2xx
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ArrivalAnswersViewModel.ArrivalAnswersViewModelProvider
import views.html.CheckArrivalsAnswersView

import scala.concurrent.{ExecutionContext, Future}

class CheckArrivalsAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckArrivalsAnswersView,
  viewModelProvider: ArrivalAnswersViewModelProvider,
  submissionService: SubmissionService,
  phaseConfig: PhaseConfig
)(implicit ec: ExecutionContext)
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
        ArrivalDomain.userAnswersReader(phaseConfig).apply(Nil).run(request.userAnswers) match {
          case Right(_) =>
            submissionService.post(mrn).map {
              case response if is2xx(response.status) =>
                Redirect(controllers.routes.DeclarationSubmittedController.onPageLoad(mrn))
              case e =>
                logger.warn(s"CheckArrivalsAnswersController:onSubmit:$mrn: ${e.status}")
                Redirect(routes.ErrorController.technicalDifficulties())
            }
          case Left(value) =>
            logger.warn(s"CheckArrivalsAnswersController:onSubmit:$mrn: Answers incomplete. Redirecting.")
            Future.successful {
              Redirect(UserAnswersNavigator.nextPage(value.page.route(request.userAnswers, NormalMode)))
            }
        }
    }

}
