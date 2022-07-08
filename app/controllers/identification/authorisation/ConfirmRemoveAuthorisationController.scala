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

import controllers.actions._
import derivable.DeriveNumberOfIdentificationAuthorisations
import forms.ConfirmRemoveItemFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.identification.authorisation._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.AuthorisationQuery
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.identification.authorisation.ConfirmRemoveAuthorisationView
import views.html.ConcurrentRemoveErrorView
import javax.inject.Inject
import navigation.annotations.IdentificationDetails

import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveAuthorisationController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @IdentificationDetails navigator: Navigator,
  formProvider: ConfirmRemoveItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  concurrentRemoveErrorView: ConcurrentRemoveErrorView,
  actions: Actions,
  view: ConfirmRemoveAuthorisationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix = "identification.authorisation.confirmRemoveAuthorisation"

  def onPageLoad(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      authorisationName(index) match {
        case Some(name) => Ok(view(formProvider(prefix, name), mrn, index, mode, name))
        case _          => renderErrorPage(mrn, index, mode)
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      authorisationName(index) match {
        case Some(name) =>
          formProvider(prefix, name)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, index, mode, name))),
              value =>
                if (value) {
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.remove(AuthorisationQuery(index)))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(ConfirmRemoveAuthorisationPage(index), mode, updatedAnswers))
                } else {
                  Future.successful(Redirect(navigator.nextPage(ConfirmRemoveAuthorisationPage(index), mode, request.userAnswers)))
                }
            )
        case _ => Future.successful(renderErrorPage(mrn, index, mode))
      }
  }

  private def authorisationName(index: Index)(implicit request: DataRequest[AnyContent]): Option[String] =
    request.userAnswers.get(AuthorisationReferenceNumberPage(index))

  private def renderErrorPage(mrn: MovementReferenceNumber, index: Index, mode: Mode)(implicit request: DataRequest[AnyContent]): Result = {
    val redirectLinkText = if (request.userAnswers.get(DeriveNumberOfIdentificationAuthorisations).contains(0)) "noAuthorisation" else "multipleAuthorisation"
    val redirectLink     = navigator.nextPage(ConfirmRemoveAuthorisationPage(index), mode, request.userAnswers).url

    NotFound(concurrentRemoveErrorView(mrn, redirectLinkText, redirectLink, "concurrent.authorisation"))
  }
}
