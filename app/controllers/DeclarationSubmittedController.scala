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

import connectors.SubmissionConnector
import controllers.actions.{Actions, SpecificDataRequiredActionProvider}
import models.MovementReferenceNumber
import pages.identification.DestinationOfficePage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import views.html.DeclarationSubmittedView

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class DeclarationSubmittedController @Inject() (
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  cc: MessagesControllerComponents,
  view: DeclarationSubmittedView,
  submissionConnector: SubmissionConnector
)(implicit ec: ExecutionContext)
    extends FrontendController(cc)
    with I18nSupport
    with Logging {

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = actions
    .requireDataIgnoreSubmissionStatus(mrn)
    .andThen(getMandatoryPage(DestinationOfficePage))
    .async {
      implicit request =>
        submissionConnector.getMessages(mrn).map {
          messages =>
            if (messages.contains("IE007")) {
              Ok(view(mrn.value, request.arg))
            } else {
              logger.warn(s"IE007 not found for MRN $mrn")
              InternalServerError
            }
        }
    }
}
