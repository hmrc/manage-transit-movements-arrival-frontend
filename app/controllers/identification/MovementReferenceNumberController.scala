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

import connectors.CacheConnector.APIVersionHeaderMismatchException
import controllers.actions.*
import forms.identification.MovementReferenceNumberFormProvider
import models.requests.IdentifierRequest
import models.{CheckMode, MovementReferenceNumber, NormalMode, SubmissionStatus, UserAnswers}
import navigation.ArrivalNavigatorProvider
import play.api.data.Form
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

  private val form = formProvider.apply()

  private val unsafeForm = formProvider.applyUnsafe()

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
      bind(unsafeForm) {
        value =>
          sessionRepository.get(value.toString).flatMap {
            userAnswers =>
              bind(form) {
                mrn =>
                  userAnswers match {
                    case None =>
                      def redirect(userAnswers: Option[UserAnswers]): Result =
                        userAnswers match {
                          case Some(value) => Redirect(navigatorProvider(NormalMode).nextPage(value, None))
                          case None        => Redirect(controllers.routes.ErrorController.technicalDifficulties())
                        }

                      sessionRepository.put(mrn.toString).flatMap {
                        _ => sessionRepository.get(mrn.toString).map(redirect)
                      }
                    case Some(userAnswers) =>
                      def redirect(userAnswers: UserAnswers): Result =
                        Redirect(navigatorProvider(CheckMode).nextPage(userAnswers, None))

                      userAnswers.submissionStatus match {
                        case SubmissionStatus.Submitted =>
                          val updatedUserAnswers = userAnswers.copy(submissionStatus = SubmissionStatus.Amending)
                          sessionRepository.set(updatedUserAnswers).map {
                            _ => redirect(updatedUserAnswers)
                          }
                        case _ =>
                          Future.successful(redirect(userAnswers))
                      }
                  }
              }
          } recover {
            case _: APIVersionHeaderMismatchException =>
              Redirect(controllers.routes.DraftNoLongerAvailableController.onPageLoad())
          }
      }
  }

  private def bind(
    form: Form[MovementReferenceNumber]
  )(
    block: MovementReferenceNumber => Future[Result]
  )(implicit request: IdentifierRequest[?]): Future[Result] =
    form
      .bindFromRequest()
      .fold(
        formWithErrors => Future.successful(BadRequest(view(formWithErrors))),
        block
      )
}
