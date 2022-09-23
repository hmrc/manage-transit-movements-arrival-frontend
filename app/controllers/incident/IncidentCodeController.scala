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
import forms.IncidentCodeFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.{IdentificationDetails, Incident}
import pages.incident.IncidentCodePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.IncidentCodeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.IncidentCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IncidentCodeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @Incident implicit val navigator: Navigator,
  actions: Actions,
  formProvider: IncidentCodeFormProvider,
  service: IncidentCodeService,
  val controllerComponents: MessagesControllerComponents,
  view: IncidentCodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getIncidentCodes.map {
        incidentCodeList =>
          val form = formProvider("incident.incidentCode", incidentCodeList)
          val preparedForm = request.userAnswers.get(IncidentCodePage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, incidentCodeList.incidentCodes, mode))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getIncidentCodes.flatMap {
        incidentCodeList =>
          val form = formProvider("incident.incidentCode", incidentCodeList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, incidentCodeList.incidentCodes, mode))),
              value => IncidentCodePage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
            )
      }
  }
}
