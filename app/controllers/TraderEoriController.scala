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
import forms.TraderEoriFormProvider
import javax.inject.Inject
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.{TraderEoriPage, TraderNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TraderEoriView

import scala.concurrent.{ExecutionContext, Future}

class TraderEoriController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: TraderEoriFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TraderEoriView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(TraderNamePage)) {
        implicit request =>
          val traderName = request.arg
          val form       = formProvider(traderName)
          val preparedForm = request.userAnswers.get(TraderEoriPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, mode, traderName))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(TraderNamePage))
      .async {
        implicit request =>
          val traderName = request.arg
          formProvider(traderName)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, traderName))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(TraderEoriPage, value.replaceAll("\\s", "").toUpperCase))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(TraderEoriPage, mode, updatedAnswers))
            )
      }
}
