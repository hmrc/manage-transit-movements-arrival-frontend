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

package controllers.events.seals

import controllers.actions._
import derivable.DeriveNumberOfSeals
import forms.events.seals.ConfirmRemoveSealFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.seals.{ConfirmRemoveSealPage, SealIdentityPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConcurrentRemoveErrorView
import views.html.events.seals.ConfirmRemoveSealView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveSealController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: ConfirmRemoveSealFormProvider,
  val controllerComponents: MessagesControllerComponents,
  concurrentRemoveErrorView: ConcurrentRemoveErrorView,
  view: ConfirmRemoveSealView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, sealIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn) {
      implicit request =>
        request.userAnswers.get(SealIdentityPage(eventIndex, sealIndex)) match {
          case Some(seal) =>
            val form = formProvider(seal)
            Ok(view(form, mrn, eventIndex, sealIndex, mode, seal.numberOrMark))
          case _ =>
            renderErrorPage(mrn, eventIndex, mode)
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, sealIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        request.userAnswers.get(SealIdentityPage(eventIndex, sealIndex)) match {
          case Some(seal) =>
            formProvider(seal)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, sealIndex, mode, seal.numberOrMark))),
                value =>
                  if (value) {
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.remove(SealIdentityPage(eventIndex, sealIndex)))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(ConfirmRemoveSealPage(eventIndex), mode, updatedAnswers))
                  } else {
                    Future.successful(Redirect(navigator.nextPage(ConfirmRemoveSealPage(eventIndex), mode, request.userAnswers)))
                  }
              )
          case _ =>
            Future.successful(renderErrorPage(mrn, eventIndex, mode))
        }
    }

  private def renderErrorPage(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode)(implicit request: DataRequest[AnyContent]): Result = {
    val redirectLinkText = if (request.userAnswers.get(DeriveNumberOfSeals(eventIndex)).contains(0)) "noSeal" else "multipleSeal"
    val redirectLink     = navigator.nextPage(ConfirmRemoveSealPage(eventIndex), mode, request.userAnswers).url

    NotFound(concurrentRemoveErrorView(mrn, redirectLinkText, redirectLink, "concurrent.seal"))
  }
}
