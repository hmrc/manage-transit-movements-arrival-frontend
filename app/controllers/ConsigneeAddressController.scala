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
import forms.ConsigneeAddressFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.{ConsigneeAddressPage, ConsigneeNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.ConsigneeAddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConsigneeAddressController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  formProvider: ConsigneeAddressFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: ConsigneeAddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(ConsigneeNamePage)) {
        implicit request =>
          val consigneeName = request.arg
          val form          = formProvider(consigneeName)
          val preparedForm = request.userAnswers.get(ConsigneeAddressPage) match {
            case Some(value) => form.fill(value)
            case None        => form
          }

          Ok(view(preparedForm, mrn, mode, consigneeName))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(ConsigneeNamePage))
      .async {
        implicit request =>
          val consigneeName = request.arg
          formProvider(consigneeName)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, consigneeName))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(ConsigneeAddressPage, value))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(ConsigneeAddressPage, mode, updatedAnswers))
            )
      }
}
