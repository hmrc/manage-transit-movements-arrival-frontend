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

package controllers

import controllers.actions.IdentifierAction
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.SessionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject

class RedirectController @Inject() (
  identify: IdentifierAction,
  cc: MessagesControllerComponents,
  sessionService: SessionService
) extends FrontendController(cc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (Action andThen identify) {
    implicit request =>
      val result = Redirect(controllers.identification.routes.MovementReferenceNumberController.onPageLoad())
      sessionService.remove(result)
  }
}
