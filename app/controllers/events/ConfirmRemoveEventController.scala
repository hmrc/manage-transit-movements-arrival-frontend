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

package controllers.events

import controllers.actions._
import derivable.DeriveNumberOfEvents
import forms.events.ConfirmRemoveEventFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber, UserAnswers}
import navigation.Navigator
import pages.events.{ConfirmRemoveEventPage, EventCountryPage, EventPlacePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import queries.EventQuery
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConcurrentRemoveErrorView
import views.html.events.ConfirmRemoveEventView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveEventController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  formProvider: ConfirmRemoveEventFormProvider,
  val controllerComponents: MessagesControllerComponents,
  concurrentRemoveErrorView: ConcurrentRemoveErrorView,
  actions: Actions,
  view: ConfirmRemoveEventView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      eventPlaceOrCountry(request.userAnswers, eventIndex) match {
        case Some(placeOrCountry) => Ok(view(formProvider(placeOrCountry), mrn, eventIndex, mode, placeOrCountry))
        case _                    => renderErrorPage(request.userAnswers, eventIndex, mode)
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      eventPlaceOrCountry(request.userAnswers, eventIndex) match {
        case Some(placeOrCountry) =>
          formProvider(placeOrCountry)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, mode, placeOrCountry))),
              value =>
                if (value) {
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.remove(EventQuery(eventIndex)))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(ConfirmRemoveEventPage(eventIndex), mode, updatedAnswers))
                } else {
                  Future.successful(Redirect(navigator.nextPage(ConfirmRemoveEventPage(eventIndex), mode, request.userAnswers)))
                }
            )
        case _ => Future.successful(renderErrorPage(request.userAnswers, eventIndex, mode))
      }
  }

  private def eventPlaceOrCountry(userAnswers: UserAnswers, eventIndex: Index): Option[String] =
    userAnswers.get(EventPlacePage(eventIndex)) match {
      case Some(answer) => Some(answer)
      case _            => userAnswers.get(EventCountryPage(eventIndex)).map(_.code)
    }

  private def renderErrorPage(userAnswers: UserAnswers, eventIndex: Index, mode: Mode)(implicit request: DataRequest[AnyContent]): Result = {
    val redirectLinkText = if (userAnswers.get(DeriveNumberOfEvents).contains(0)) "noEvent" else "multipleEvent"
    val redirectLink     = navigator.nextPage(ConfirmRemoveEventPage(eventIndex), mode, userAnswers).url

    NotFound(concurrentRemoveErrorView(redirectLinkText, redirectLink, "concurrent.event"))
  }
}
