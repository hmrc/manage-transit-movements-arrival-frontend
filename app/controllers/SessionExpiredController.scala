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

import config.FrontendAppConfig
import models.MovementReferenceNumber
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.SessionExpiredView

import javax.inject.Inject

class SessionExpiredController @Inject() (
  val controllerComponents: MessagesControllerComponents,
  val config: FrontendAppConfig,
  view: SessionExpiredView
) extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: Option[MovementReferenceNumber]): Action[AnyContent] = Action {
    implicit request =>
      Ok(view(mrn))
  }

  def onSubmit(mrn: Option[MovementReferenceNumber]): Action[AnyContent] = Action {
    _ =>
      val url = s"${config.manageTransitMovementsUrl}/what-do-you-want-to-do"

      mrn match
        case Some(value) => Redirect(controllers.routes.DeleteLockController.delete(value, Some(RedirectUrl(url)))).withNewSession
        case None        => Redirect(url).withNewSession
  }
}
