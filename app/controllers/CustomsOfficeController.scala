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
import forms.CustomsOfficeFormProvider
import models.requests.DataRequest
import models.{CustomsOfficeList, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.{ConsigneeNamePage, CustomsOfficePage, CustomsSubPlacePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import services.CustomsOfficesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CustomsOfficeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsOfficeController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: CustomsOfficeFormProvider,
  customsOfficesService: CustomsOfficesService,
  val controllerComponents: MessagesControllerComponents,
  view: CustomsOfficeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      customsOfficesService.getCustomsOfficesOfArrival.map {
        customsOfficeList: CustomsOfficeList =>
          locationName match {
            case Some(locationName) =>
              val form = formProvider(locationName, customsOfficeList)
              val preparedForm = request.userAnswers.get(CustomsOfficePage) match {
                case None        => form
                case Some(value) => form.fill(value)
              }
              Ok(view(preparedForm, customsOfficeList.customsOffices, mrn, mode, locationName))
            case _ =>
              Redirect(routes.SessionExpiredController.onPageLoad())
          }
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      customsOfficesService.getCustomsOfficesOfArrival.flatMap {
        customsOfficeList =>
          locationName match {
            case Some(locationName) =>
              val form = formProvider(locationName, customsOfficeList)
              form
                .bindFromRequest()
                .fold(
                  formWithErrors => Future.successful(BadRequest(view(formWithErrors, customsOfficeList.customsOffices, mrn, mode, locationName))),
                  value =>
                    for {
                      updatedAnswers <- Future.fromTry(request.userAnswers.set(CustomsOfficePage, value))
                      _              <- sessionRepository.set(updatedAnswers)
                    } yield Redirect(navigator.nextPage(CustomsOfficePage, mode, updatedAnswers))
                )
            case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))

          }
      }
  }

  private def locationName(implicit request: DataRequest[AnyContent]): Option[String] =
    request.userAnswers
      .get(CustomsSubPlacePage)
      .orElse(request.userAnswers.get(ConsigneeNamePage))
      .orElse(None)

}
