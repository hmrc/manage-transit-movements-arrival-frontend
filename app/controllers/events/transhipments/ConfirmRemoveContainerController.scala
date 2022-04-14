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

package controllers.events.transhipments

import config.annotations.Container
import controllers.actions.Actions
import derivable.DeriveNumberOfContainers
import forms.events.transhipments.ConfirmRemoveContainerFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.transhipments.{ConfirmRemoveContainerPage, ContainerNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConcurrentRemoveErrorView
import views.html.events.transhipments.ConfirmRemoveContainerView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveContainerController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @Container navigator: Navigator,
  actions: Actions,
  concurrentRemoveErrorView: ConcurrentRemoveErrorView,
  formProvider: ConfirmRemoveContainerFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmRemoveContainerView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, containerIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn) {
      implicit request =>
        request.userAnswers.get(ContainerNumberPage(eventIndex, containerIndex)) match {
          case Some(container) =>
            val form = formProvider(container)
            Ok(view(form, mrn, eventIndex, containerIndex, mode, container.containerNumber))
          case _ =>
            renderErrorPage(mrn, eventIndex, mode)
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, containerIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        request.userAnswers.get(ContainerNumberPage(eventIndex, containerIndex)) match {
          case Some(container) =>
            formProvider(container)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, containerIndex, mode, container.containerNumber))),
                value =>
                  if (value) {
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.remove(ContainerNumberPage(eventIndex, containerIndex)))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(ConfirmRemoveContainerPage(eventIndex), mode, updatedAnswers))
                  } else {
                    Future.successful(Redirect(navigator.nextPage(ConfirmRemoveContainerPage(eventIndex), mode, request.userAnswers)))
                  }
              )
          case _ =>
            Future.successful(renderErrorPage(mrn, eventIndex, mode))
        }
    }

  private def renderErrorPage(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode)(implicit request: DataRequest[AnyContent]): Result = {
    val redirectLinkText = if (request.userAnswers.get(DeriveNumberOfContainers(eventIndex)).contains(0)) "noContainer" else "multipleContainer"
    val redirectLink     = navigator.nextPage(ConfirmRemoveContainerPage(eventIndex), mode, request.userAnswers).url

    NotFound(concurrentRemoveErrorView(mrn, redirectLinkText, redirectLink, "concurrent.container"))
  }

}
