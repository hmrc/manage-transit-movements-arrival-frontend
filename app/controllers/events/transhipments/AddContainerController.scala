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

import config.FrontendAppConfig
import controllers.actions._
import derivable.DeriveNumberOfContainers
import forms.events.transhipments.AddContainerFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.transhipments.AddContainerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import utils.AddContainerHelper
import views.html.events.transhipments.AddContainerView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddContainerController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: AddContainerFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddContainerView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val form = formProvider(allowMoreContainers(eventIndex))
      val preparedForm = request.userAnswers.get(AddContainerPage(eventIndex)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mrn, eventIndex, mode, containers, allowMoreContainers))
  }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      formProvider(allowMoreContainers(eventIndex))
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, mode, containers, allowMoreContainers))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AddContainerPage(eventIndex), value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(AddContainerPage(eventIndex), mode, updatedAnswers))
        )
  }

  private def containers(eventIndex: Index, mode: Mode)(implicit request: DataRequest[_]): Seq[ListItem] = {
    val addContainerHelper = new AddContainerHelper(request.userAnswers, mode)
    (0 to numberOfContainers(eventIndex)) flatMap {
      x => addContainerHelper.containerListItem(eventIndex, Index(x))
    }
  }

  private def allowMoreContainers(eventIndex: Index)(implicit request: DataRequest[_]): Boolean =
    numberOfContainers(eventIndex) < config.maxContainers

  private def numberOfContainers(eventIndex: Index)(implicit request: DataRequest[_]): Int =
    request.userAnswers.get(DeriveNumberOfContainers(eventIndex)).getOrElse(0)
}
