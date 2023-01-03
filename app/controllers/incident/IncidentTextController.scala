/*
 * Copyright 2023 HM Revenue & Customs
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
import forms.incident.IncidentTextFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.IncidentTextPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.IncidentTextView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IncidentTextController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  formProvider: IncidentTextFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: IncidentTextView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("incident.incidentText")

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(IncidentTextPage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mrn, mode, index))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, index))),
          value => {
            implicit lazy val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
            IncidentTextPage(index).writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }
}
