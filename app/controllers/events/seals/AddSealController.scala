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

import config.FrontendAppConfig
import config.annotations.Seal
import controllers.actions.Actions
import derivable.DeriveNumberOfSeals
import forms.events.seals.AddSealFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.seals.AddSealPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddSealHelper
import views.html.events.seals.AddSealView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddSealController @Inject() (
  override val messagesApi: MessagesApi,
  @Seal navigator: Navigator,
  actions: Actions,
  formProvider: AddSealFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddSealView,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val form = formProvider(allowMoreSeals(eventIndex))

      Ok(view(form, mrn, eventIndex, mode, seals, allowMoreSeals))
  }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      formProvider(allowMoreSeals(eventIndex))
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, mode, seals, allowMoreSeals))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(AddSealPage(eventIndex), value))
            } yield Redirect(navigator.nextPage(AddSealPage(eventIndex), mode, updatedAnswers))
        )
  }

  private def seals(eventIndex: Index, mode: Mode)(implicit request: DataRequest[_]): Seq[ListItem] = {
    val addSealHelper = new AddSealHelper(request.userAnswers, mode)
    (0 to numberOfSeals(eventIndex)) flatMap {
      x => addSealHelper.sealListItem(eventIndex, Index(x))
    }
  }

  private def allowMoreSeals(eventIndex: Index)(implicit request: DataRequest[_]): Boolean =
    numberOfSeals(eventIndex) < config.maxSeals

  private def numberOfSeals(eventIndex: Index)(implicit request: DataRequest[_]): Int =
    request.userAnswers.get(DeriveNumberOfSeals(eventIndex)).getOrElse(0)
}
