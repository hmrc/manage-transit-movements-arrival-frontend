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

package controllers.incident.equipment.itemNumber

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.YesNoFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Index, Mode, MovementReferenceNumber}
import pages.incident.equipment.itemNumber.ItemNumberPage
import pages.sections.incident.ItemSection
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.equipment.itemNumber.ConfirmRemoveItemNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveItemNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConfirmRemoveItemNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def addAnother(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Call =
    routes.AddAnotherItemNumberYesNoController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex)

  private def form(implicit request: Request): Form[Boolean] =
    formProvider("incident.equipment.itemNumber.remove", request.arg)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index, itemNumberIndex: Index): Action[AnyContent] = actions
    .requireIndex(mrn, ItemSection(incidentIndex, equipmentIndex, itemNumberIndex), addAnother(mrn, mode, incidentIndex, equipmentIndex))
    .andThen(getMandatoryPage(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex))) {
      implicit request =>
        Ok(view(form, mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex, request.arg))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index, itemNumberIndex: Index): Action[AnyContent] = actions
    .requireIndex(mrn, ItemSection(incidentIndex, equipmentIndex, itemNumberIndex), addAnother(mrn, mode, incidentIndex, equipmentIndex))
    .andThen(getMandatoryPage(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex)))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex, request.arg))),
            {
              case true =>
                ItemSection(incidentIndex, equipmentIndex, itemNumberIndex)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(addAnother(mrn, mode, incidentIndex, equipmentIndex))
              case false =>
                Future.successful(Redirect(addAnother(mrn, mode, incidentIndex, equipmentIndex)))
            }
          )
    }
}
