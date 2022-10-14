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
import forms.identification.AuthorisationRefNoFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{AuthorisationNavigator, AuthorisationNavigatorProvider, UserAnswersNavigator}
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.identification.authorisation.AuthorisationReferenceNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AuthorisationReferenceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: AuthorisationNavigatorProvider,
  formProvider: AuthorisationRefNoFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: AuthorisationReferenceNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage.getFirst(AuthorisationTypePage(index))) {
        implicit request =>
          val form = formProvider("identification.authorisation.authorisationReferenceNumber", request.arg.toString)

          val preparedForm = request.userAnswers.get(AuthorisationReferenceNumberPage(index)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, index, request.arg.toString, mode))
      }

  def onSubmit(mrn: MovementReferenceNumber, index: Index, mode: Mode): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage.getFirst(AuthorisationTypePage(index)))
      .async {
        implicit request =>
          val form = formProvider("identification.authorisation.authorisationReferenceNumber", request.arg.toString)

          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, index: Index, request.arg.toString, mode))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                AuthorisationReferenceNumberPage(index).writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
}
