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

package controllers.incident.equipment

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import pages.sections.incident.EquipmentSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.equipment.ConfirmRemoveEquipmentView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveEquipmentController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  actions: Actions,
  view: ConfirmRemoveEquipmentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions
    .requireData(mrn) {
      implicit request =>
        val form = formProvider("incident.equipment.remove", equipmentIndex.display)

        Ok(view(form, mrn, mode, incidentIndex, equipmentIndex))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .async {
      implicit request =>
        val form = formProvider("incident.equipment.remove", equipmentIndex.display)

        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, incidentIndex, equipmentIndex))),
            {
              case true =>
                EquipmentSection(incidentIndex, equipmentIndex)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(routes.AddAnotherEquipmentController.onPageLoad(mrn, mode, incidentIndex))
              case false =>
                Future.successful(Redirect(routes.AddAnotherEquipmentController.onPageLoad(mrn, mode, incidentIndex)))
            }
          )
    }
}
