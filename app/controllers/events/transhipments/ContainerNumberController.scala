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

import controllers.actions._
import forms.events.transhipments.ContainerNumberFormProvider
import models.domain.ContainerDomain
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.transhipments.ContainerNumberPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.ContainersQuery
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.events.transhipments.ContainerNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContainerNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  formProvider: ContainerNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ContainerNumberView,
  actions: Actions
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, containerIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn) {
      implicit request =>
        val preparedForm = request.userAnswers.get(ContainerNumberPage(eventIndex, containerIndex)) match {
          case None        => formProvider(containerIndex)
          case Some(value) => formProvider(containerIndex).fill(value.containerNumber)
        }
        Ok(view(preparedForm, mrn, eventIndex, containerIndex, mode))
    }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, containerIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        val containers = request.userAnswers.get(ContainersQuery(eventIndex)).getOrElse(Seq.empty)

        formProvider(containerIndex, containers)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, containerIndex, mode))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(ContainerNumberPage(eventIndex, containerIndex), ContainerDomain(value)))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(ContainerNumberPage(eventIndex, containerIndex), mode, updatedAnswers))
          )
    }
}
