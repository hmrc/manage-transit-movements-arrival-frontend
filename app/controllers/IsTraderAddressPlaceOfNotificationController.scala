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

class IsTraderAddressPlaceOfNotificationController @Inject() (override val messagesApi: MessagesApi,
                                                              sessionRepository: SessionRepository,
                                                              navigator: Navigator,
                                                              actions: Actions,
                                                              formProvider: IsTraderAddressPlaceOfNotificationFormProvider,
                                                              val controllerComponents: MessagesControllerComponents,
                                                              view: IsTraderAddressPlaceOfNotificationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      request.userAnswers.get(TraderAddressPage) match {
        case Some(traderAddress) =>
          val traderName = request.userAnswers.get(TraderNamePage).getOrElse("")
          val form       = formProvider(traderName)

          val preparedForm = request.userAnswers.get(IsTraderAddressPlaceOfNotificationPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, mode, traderName, traderAddress))

        case _ => Redirect(routes.SessionExpiredController.onPageLoad())
      }

  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      request.userAnswers.get(TraderAddressPage) match {
        case Some(traderAddress) =>
          val traderName = request.userAnswers.get(TraderNamePage).getOrElse("")
          val form       = formProvider(traderName)

          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, traderName, traderAddress))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(IsTraderAddressPlaceOfNotificationPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedAnswers))
            )
        case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
  }
}
