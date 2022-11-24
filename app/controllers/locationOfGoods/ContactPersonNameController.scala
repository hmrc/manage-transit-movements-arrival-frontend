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

package controllers.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.NameFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.{ArrivalNavigatorProvider, UserAnswersNavigator}
import pages.locationOfGoods.ContactPersonNamePage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.locationOfGoods.ContactPersonNameView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContactPersonNameController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ArrivalNavigatorProvider,
  formProvider: NameFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: ContactPersonNameView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("locationOfGoods.contactPersonName")

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val preparedForm = request.userAnswers.get(ContactPersonNamePage) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mrn, mode))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
            ContactPersonNamePage.writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }
}
