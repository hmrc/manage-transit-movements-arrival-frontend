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

import config.FrontendAppConfig
import controllers.actions._

import javax.inject.Inject
import models.GoodsLocation.AuthorisedConsigneesLocation
import models.MovementReferenceNumber
import models.reference.CustomsOffice
import pages.{CustomsOfficePage, GoodsLocationPage}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Request}
import renderer.Renderer
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.viewmodels.NunjucksSupport
import views.html.ArrivalCompleteView

import scala.concurrent.{ExecutionContext, Future}

class ConfirmationController @Inject() (override val messagesApi: MessagesApi,
                                        appConfig: FrontendAppConfig,
                                        sessionRepository: SessionRepository,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalActionProvider,
                                        requireData: DataRequiredAction,
                                        val controllerComponents: MessagesControllerComponents,
                                        renderer: Renderer,
                                        arrivalCompleteView: ArrivalCompleteView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with NunjucksSupport {

  private def contactUsMessage(office: CustomsOffice)(implicit request: Request[_]) = (office.name, office.phoneNumber) match {
    case (Some(office), Some(telephone)) =>
      Messages("arrivalComplete.contact.withOfficeAndPhoneNumber", office, telephone)
    case (Some(office), None) =>
      Messages("arrivalComplete.contact.withOffice", office)
    case _ =>
      Messages("arrivalComplete.contact.withOfficeCode", office.id)
  }

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData).async {
    implicit request =>
      request.userAnswers.get(CustomsOfficePage) match {
        case Some(customsOffice) =>
          val paragraph = if (request.userAnswers.get(GoodsLocationPage).contains(AuthorisedConsigneesLocation)) {
            Messages("arrivalComplete.para1.simplified")
          } else {
            Messages("arrivalComplete.para1.normal")
          }

          sessionRepository.remove(mrn.toString, request.eoriNumber).map {
            _ =>
              Ok(arrivalCompleteView(mrn, paragraph, contactUsMessage(customsOffice)))
          }
        case _ =>
          Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }

}
