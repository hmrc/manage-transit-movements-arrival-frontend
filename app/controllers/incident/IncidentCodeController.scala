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

package controllers.incident

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.incident.IncidentCodeFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import models.incident.IncidentCode
import navigation.{IncidentNavigator, IncidentNavigatorProvider, Navigator}
import navigation.annotations.IdentificationDetails
import pages.incident.IncidentCodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.IncidentCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IncidentCodeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  formProvider: IncidentCodeFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IncidentCodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(IncidentCodePage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mrn, IncidentCode.radioItems, mode, index))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, IncidentCode.radioItems, mode, index))),
          value => {
            implicit val navigator: IncidentNavigator = navigatorProvider(index)
            IncidentCodePage(index).writeToUserAnswers(value).writeToSession().navigateWith(mode)
          }
        )
  }
}
