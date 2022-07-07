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

package controllers.identification

import com.google.inject.Inject
import controllers.actions.Actions
import models.{CheckMode, MovementReferenceNumber}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.identification.CheckIdentificationAnswersViewModel
import views.html.identification.CheckIdentificationAnswersView

class CheckIdentificationAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: CheckIdentificationAnswersView,
  viewModel: CheckIdentificationAnswersViewModel
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val sections = viewModel(request.userAnswers, CheckMode)
      Ok(view(mrn, sections))
  }

  def onSubmit(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn) {
    _ =>
      Redirect(controllers.identification.routes.MovementReferenceNumberController.onPageLoad())
  }

}
