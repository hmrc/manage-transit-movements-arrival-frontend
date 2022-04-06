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

import controllers.actions.Actions._
import controllers.actions._
import forms.EoriNumberFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.{ConsigneeEoriNumberPage, ConsigneeNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.EoriNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConsigneeEoriNumberController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: EoriNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EoriNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).apply {
    implicit request =>
      getPage(ConsigneeNamePage) {
        consigneeName =>
          val form = formProvider(consigneeName)
          val preparedForm = request.userAnswers.get(ConsigneeEoriNumberPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, mode, consigneeName))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      getPageF(ConsigneeNamePage) {
        consigneeName =>
          formProvider(consigneeName)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, consigneeName))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(ConsigneeEoriNumberPage, value.replaceAll("\\s", "").toUpperCase))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(ConsigneeEoriNumberPage, mode, updatedAnswers))
            )
      }
  }
}
