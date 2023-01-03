/*
 * Copyright 2023 HM Revenue & Customs
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

package controllers.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.TelephoneNumberFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.{ArrivalNavigatorProvider, UserAnswersNavigator}
import pages.locationOfGoods.{ContactPersonNamePage, ContactPersonTelephonePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.locationOfGoods.ContactPersonTelephoneView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContactPersonTelephoneController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ArrivalNavigatorProvider,
  formProvider: TelephoneNumberFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  view: ContactPersonTelephoneView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(ContactPersonNamePage)) {
      implicit request =>
        val form = formProvider("locationOfGoods.contactPersonTelephone", request.arg)
        val preparedForm = request.userAnswers.get(ContactPersonTelephonePage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }
        Ok(view(preparedForm, mrn, request.arg, mode))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(ContactPersonNamePage))
    .async {
      implicit request =>
        val form = formProvider("locationOfGoods.contactPersonTelephone", request.arg)
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, request.arg, mode))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
              ContactPersonTelephonePage.writeToUserAnswers(value).writeToSession().navigate()
            }
          )
    }
}
