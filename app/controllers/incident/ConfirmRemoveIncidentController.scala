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
import forms.YesNoFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import pages.incident.IncidentTextPage
import pages.sections.incident.IncidentSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.ConfirmRemoveIncidentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveIncidentController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmRemoveIncidentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def addAnother(mrn: MovementReferenceNumber, mode: Mode): Call =
    routes.AddAnotherIncidentController.onPageLoad(mrn, mode)

  private def form(index: Index): Form[Boolean] =
    formProvider("incident.remove", index.display)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions
    .requireIndex(mrn, IncidentSection(incidentIndex), addAnother(mrn, mode)) {
      implicit request =>
        val incidentDescription = request.userAnswers.get(IncidentTextPage(incidentIndex))
        Ok(view(form(incidentIndex), mrn, mode, incidentIndex, incidentDescription))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions
    .requireIndex(mrn, IncidentSection(incidentIndex), addAnother(mrn, mode))
    .async {
      implicit request =>
        form(incidentIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => {
              val incidentDescription = request.userAnswers.get(IncidentTextPage(incidentIndex))
              Future.successful(BadRequest(view(formWithErrors, mrn, mode, incidentIndex, incidentDescription)))
            },
            {
              case true =>
                IncidentSection(incidentIndex)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(addAnother(mrn, mode))
              case false =>
                Future.successful(Redirect(addAnother(mrn, mode)))
            }
          )
    }
}
