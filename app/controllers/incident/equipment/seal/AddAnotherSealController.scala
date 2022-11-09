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

package controllers.incident.equipment.seal

import config.FrontendAppConfig
import controllers.actions._
import forms.AddItemFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.EquipmentNavigatorProvider
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.incident.AddAnotherSealViewModel.AddAnotherSealViewModelProvider
import views.html.incident.equipment.seal.AddAnotherSealView

import javax.inject.Inject

class AddAnotherSealController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: EquipmentNavigatorProvider,
  actions: Actions,
  formProvider: AddItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherSealViewModelProvider,
  view: AddAnotherSealView
) extends FrontendBaseController
    with I18nSupport {

  // TODO - need dynamic heading and title

  private def form(allowMoreSeals: Boolean): Form[Boolean] =
    formProvider("incident.equipment.seal.addAnotherSeal", allowMoreSeals)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions
    .requireData(mrn) {
      implicit request =>
        val (seals, numberOfSeals, allowMoreSeals) = viewData(mode, incidentIndex, equipmentIndex)
        numberOfSeals match {
          case 0 => Redirect(controllers.incident.equipment.routes.AddSealsYesNoController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex))
          case _ => Ok(view(form(allowMoreSeals), mrn, mode, incidentIndex, equipmentIndex, seals, allowMoreSeals))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      lazy val (seals, numberOfSeals, allowMoreSeals) = viewData(mode, incidentIndex, equipmentIndex)
      form(allowMoreSeals)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, mrn, mode, incidentIndex, equipmentIndex, seals, allowMoreSeals)),
          {
            case true  => Redirect(routes.SealIdentificationNumberController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex, Index(numberOfSeals)))
            case false => Redirect(navigatorProvider(mode, incidentIndex, equipmentIndex).nextPage(request.userAnswers))
          }
        )
  }

  private def viewData(mode: Mode, incidentIndex: Index, equipmentIndex: Index)(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val seals         = viewModelProvider.apply(request.userAnswers, mode, incidentIndex, equipmentIndex).listItems
    val numberOfSeals = seals.length
    (seals, numberOfSeals, numberOfSeals < config.maxSeals)
  }
}
