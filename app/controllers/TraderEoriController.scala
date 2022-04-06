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
import models.{Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.{TraderEoriPage, TraderNamePage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.TraderEoriView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TraderEoriController @Inject() (override val messagesApi: MessagesApi,
                                      sessionRepository: SessionRepository,
                                      navigator: Navigator,
                                      identify: IdentifierAction,
                                      getData: DataRetrievalActionProvider,
                                      requireData: DataRequiredAction,
                                      formProvider: TraderEoriFormProvider,
                                      val controllerComponents: MessagesControllerComponents,
                                      view: TraderEoriView,
                                      actions: Actions
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn) {
      implicit request =>
        val traderName = request.userAnswers.get(TraderNamePage).getOrElse("")

        val form = formProvider(traderName)

        val preparedForm = request.userAnswers.get(TraderEoriPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mrn, mode, traderName))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        val traderName = request.userAnswers.get(TraderNamePage).getOrElse("")
        val form       = formProvider(traderName)

        form
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
