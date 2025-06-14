/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.actions.Actions
import models.MovementReferenceNumber
import scala.util.control.NonFatal
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.LockService
import uk.gov.hmrc.play.bootstrap.binders.RedirectUrl.idFunctor
import uk.gov.hmrc.play.bootstrap.binders.{AbsoluteWithHostnameFromAllowlist, OnlyRelative, RedirectUrl}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeleteLockController @Inject() (
  actions: Actions,
  cc: MessagesControllerComponents,
  lockService: LockService,
  appConfig: FrontendAppConfig
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  def delete(mrn: MovementReferenceNumber, continue: Option[RedirectUrl]): Action[AnyContent] =
    actions.requireDataNoLock(mrn).async {
      implicit request =>
        val redirectUrlPolicy = AbsoluteWithHostnameFromAllowlist(appConfig.allowedRedirectUrls*) | OnlyRelative

        val url = continue.map(_.get(redirectUrlPolicy).url).getOrElse(appConfig.signOutUrl)

        lockService.deleteLock(request.userAnswers).map {
          _ =>
            Redirect(url)
        } recover {
          case NonFatal(exception) =>
            logger.info(s"Failed to unlock session for MRN $mrn", exception)
            Redirect(url)
        }
    }
}
