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
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.identification.authorisation.AuthorisationTypeFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import models.identification.authorisation.AuthorisationType
import navigation.Navigator
import navigation.annotations.IdentificationDetails
import pages.identification.authorisation.AuthorisationTypePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.identification.authorisation.AuthorisationTypeView
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

class AuthorisationTypeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @IdentificationDetails implicit val navigator: Navigator,
  actions: Actions,
  formProvider: AuthorisationTypeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AuthorisationTypeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(AuthorisationTypePage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mrn, index, AuthorisationType.radioItems, mode))
  }

  def onSubmit(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, index, AuthorisationType.radioItems, mode))),
          value => AuthorisationTypePage(index).writeToUserAnswers(value).writeToSession().navigateWith(mode)
        )
  }
}
