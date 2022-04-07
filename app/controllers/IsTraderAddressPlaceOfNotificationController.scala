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
import forms.IsTraderAddressPlaceOfNotificationFormProvider
import javax.inject.Inject
import models._
import navigation.Navigator
import pages.{IsTraderAddressPlaceOfNotificationPage, TraderAddressPage, TraderNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IsTraderAddressPlaceOfNotificationView

import scala.concurrent.{ExecutionContext, Future}

class IsTraderAddressPlaceOfNotificationController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: IsTraderAddressPlaceOfNotificationFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IsTraderAddressPlaceOfNotificationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions.requireSpecificData2(mrn, TraderNamePage, TraderAddressPage).apply {
      implicit request =>
        val traderAddress = request.arg
        val traderName    = request.request.arg
        val form          = formProvider(traderName)
        val preparedForm = request.userAnswers.get(IsTraderAddressPlaceOfNotificationPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mrn, mode, traderName, traderAddress))

    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions.requireSpecificData2(mrn, TraderNamePage, TraderAddressPage).async {
      implicit request =>
        val traderAddress = request.arg
        val traderName    = request.request.arg
        formProvider(traderName)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, traderName, traderAddress))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(IsTraderAddressPlaceOfNotificationPage, value))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedAnswers))
          )
    }
}
