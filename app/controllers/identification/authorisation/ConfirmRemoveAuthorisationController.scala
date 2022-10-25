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

package controllers.identification.authorisation

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.ConfirmRemoveItemFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import pages.identification.authorisation._
import pages.sections.identification
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.identification.authorisation.ConfirmRemoveAuthorisationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ConfirmRemoveAuthorisationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  formProvider: ConfirmRemoveItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: ConfirmRemoveAuthorisationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage.getFirst(AuthorisationReferenceNumberPage(index)))
    .andThen(getMandatoryPage.getSecond(AuthorisationTypePage(index))) {
      implicit request =>
        val form = formProvider("identification.authorisation.confirmRemoveAuthorisation", request.arg._1, request.arg._2)

        Ok(view(form, mrn, index, mode, request.arg._1, request.arg._2.toString))
    }

  def onSubmit(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage.getFirst(AuthorisationReferenceNumberPage(index)))
    .andThen(getMandatoryPage.getSecond(AuthorisationTypePage(index)))
    .async {
      implicit request =>
        val form = formProvider("identification.authorisation.confirmRemoveAuthorisation", request.arg._1, request.arg._2)

        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, index, mode, request.arg._1, request.arg._2.toString))),
            {
              case true =>
                identification
                  .AuthorisationSection(index)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(routes.AddAnotherAuthorisationController.onPageLoad(mrn, mode))
              case false =>
                Future.successful(Redirect(routes.AddAnotherAuthorisationController.onPageLoad(mrn, mode)))
            }
          )
    }
}
