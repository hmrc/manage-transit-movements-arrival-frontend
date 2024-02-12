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

package controllers.incident.equipment

import com.google.inject.Inject
import controllers.actions.Actions
import models.{Index, Mode, MovementReferenceNumber}
import navigation.EquipmentsNavigatorProvider
import pages.sections.incident.EquipmentSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.incident.EquipmentAnswersViewModel.EquipmentAnswersViewModelProvider
import views.html.incident.equipment.CheckEquipmentAnswersView

class CheckEquipmentAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  navigatorProvider: EquipmentsNavigatorProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckEquipmentAnswersView,
  viewModelProvider: EquipmentAnswersViewModelProvider
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val sections = viewModelProvider(request.userAnswers, incidentIndex, equipmentIndex, mode).sections
      Ok(view(mrn, mode, incidentIndex, equipmentIndex, sections))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      Redirect(navigatorProvider(mode, incidentIndex).nextPage(request.userAnswers, Some(EquipmentSection(incidentIndex, equipmentIndex))))
  }

}
