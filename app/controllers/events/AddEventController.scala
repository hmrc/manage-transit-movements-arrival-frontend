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

import config.FrontendAppConfig
import controllers.actions._
import derivable.DeriveNumberOfEvents
import forms.events.AddEventFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.AddEventPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddEventsHelper
import views.html.events.AddEventView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddEventController @Inject() (
  override val messagesApi: MessagesApi,
  navigator: Navigator,
  actions: Actions,
  formProvider: AddEventFormProvider,
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  view: AddEventView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val form = formProvider(allowMoreEvents)
      val preparedForm = request.userAnswers.get(AddEventPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mrn, mode, events, allowMoreEvents))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      formProvider(allowMoreEvents)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, events, allowMoreEvents))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AddEventPage, value))
            } yield Redirect(navigator.nextPage(AddEventPage, mode, updatedAnswers))
        )
  }

  private def events(mode: Mode)(implicit request: DataRequest[_]): Seq[ListItem] = {
    val addEventsHelper = new AddEventsHelper(request.userAnswers, mode)
    (0 to numberOfEvents) flatMap {
      x => addEventsHelper.eventListItem(Index(x))
    }
  }

  private def allowMoreEvents(implicit request: DataRequest[_]): Boolean =
    numberOfEvents < config.maxEvents

  private def numberOfEvents(implicit request: DataRequest[_]): Int =
    request.userAnswers.get(DeriveNumberOfEvents).getOrElse(0)
}
