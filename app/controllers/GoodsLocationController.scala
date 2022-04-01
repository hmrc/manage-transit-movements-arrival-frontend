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
import forms.GoodsLocationFormProvider
import models.GoodsLocation.{AuthorisedConsigneesLocation, BorderForceOffice}
import models.{GoodsLocation, Mode, MovementReferenceNumber}
import pages.GoodsLocationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.GoodsLocationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GoodsLocationController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  identify: IdentifierAction,
  getData: DataRetrievalActionProvider,
  requireData: DataRequiredAction,
  formProvider: GoodsLocationFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: GoodsLocationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider()

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.get(GoodsLocationPage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, GoodsLocation.radioItems, mrn, mode))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = (identify andThen getData(mrn) andThen requireData).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, GoodsLocation.radioItems, mrn, mode))),
          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(GoodsLocationPage, value))
              _              <- sessionRepository.set(updatedAnswers)
            } yield value match {
              case BorderForceOffice            => Redirect(routes.CustomsSubPlaceController.onPageLoad(updatedAnswers.movementReferenceNumber, mode))
              case AuthorisedConsigneesLocation => Redirect(routes.AuthorisedLocationController.onPageLoad(updatedAnswers.movementReferenceNumber, mode))
            }
        )
  }
}
