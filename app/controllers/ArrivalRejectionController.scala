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

package controllers

import controllers.actions._
import handlers.ErrorHandler
import models.ArrivalId
import models.messages.ErrorType.MRNError
import models.messages.FunctionalError
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.ArrivalRejectionService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.{ArrivalGeneralRejectionView, MovementReferenceNumberRejectionView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ArrivalRejectionController @Inject() (
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  arrivalRejectionService: ArrivalRejectionService,
  errorHandler: ErrorHandler,
  mrnRejectionView: MovementReferenceNumberRejectionView,
  arrivalRejectionView: ArrivalGeneralRejectionView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(arrivalId: ArrivalId): Action[AnyContent] = identify.async {
    implicit request =>
      arrivalRejectionService.arrivalRejectionMessage(arrivalId).flatMap {
        case Some(rejectionMessage) =>
          Future.successful {
            rejectionMessage.errors match {
              case FunctionalError(mrnError: MRNError, _, _, _) :: Nil =>
                Ok(mrnRejectionView(arrivalId, mrnError, rejectionMessage.movementReferenceNumber))
              case errors =>
                Ok(arrivalRejectionView(errors))
            }
          }
        case _ => errorHandler.onClientError(request, INTERNAL_SERVER_ERROR)
      }
  }
}
