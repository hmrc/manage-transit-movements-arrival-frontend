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
import models.requests.SpecificDataRequestProvider1
import models.{Index, MovementReferenceNumber}
import pages.identification.authorisation._
import pages.sections.AuthorisationSection
import play.api.data.Form
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

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def form(implicit request: Request): Form[Boolean] =
    formProvider("identification.authorisation.confirmRemoveAuthorisation", request.arg)

  def onPageLoad(mrn: MovementReferenceNumber, index: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(AuthorisationReferenceNumberPage(index))) {
      implicit request =>
        Ok(view(form, mrn, index, request.arg))
    }

  def onSubmit(mrn: MovementReferenceNumber, index: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(AuthorisationReferenceNumberPage(index)))
    .async {
      implicit request =>
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, index, request.arg))),
            {
              case true =>
                AuthorisationSection(index)
                  .removeFromUserAnswers()
                  .writeToSession()
                  .navigateTo(controllers.identification.routes.AddAnotherAuthorisationController.onPageLoad(mrn))
              case false =>
                Future.successful(Redirect(controllers.identification.routes.AddAnotherAuthorisationController.onPageLoad(mrn)))
            }
          )
    }
}
