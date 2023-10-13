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

package controllers.identification

import controllers.actions._
import forms.identification.MovementReferenceNumberFormProvider
import models.{CheckMode, Mode, MovementReferenceNumber, NormalMode, UserAnswers}
import navigation.ArrivalNavigatorProvider
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.identification.MovementReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MovementReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigatorProvider: ArrivalNavigatorProvider,
  identify: IdentifierAction,
  formProvider: MovementReferenceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: MovementReferenceNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form))
  }

  def onPageReload(mrn: MovementReferenceNumber): Action[AnyContent] = identify {
    implicit request =>
      Ok(view(form.fill(mrn)))
  }

  def onSubmit(): Action[AnyContent] = identify.async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
          value => {
            def redirect(userAnswers: Option[UserAnswers], mode: Mode): Result =
              userAnswers match {
                case Some(value) => Redirect(navigatorProvider(mode).nextPage(value))
                case None        => Redirect(controllers.routes.ErrorController.technicalDifficulties())
              }

            sessionRepository.get(value.toString).flatMap {
              case None =>
                sessionRepository.put(value.toString).flatMap {
                  _ => sessionRepository.get(value.toString).map(redirect(_, NormalMode))
                }
              case userAnswers =>
                Future.successful(redirect(userAnswers, CheckMode))
            }
          }
        )
  }
}
