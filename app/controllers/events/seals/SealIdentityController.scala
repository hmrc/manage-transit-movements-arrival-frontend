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

package controllers.events.seals

import controllers.actions._
import forms.events.seals.SealIdentityFormProvider
import javax.inject.Inject
import models.domain.SealDomain
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.seals.SealIdentityPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import queries.SealsQuery
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.events.seals.SealIdentityView

import scala.concurrent.{ExecutionContext, Future}

class SealIdentityController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: SealIdentityFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SealIdentityView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, sealIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn) {
      implicit request =>
        val preparedForm = request.userAnswers.get(SealIdentityPage(eventIndex, sealIndex)) match {
          case None        => form(sealIndex)
          case Some(value) => form(sealIndex).fill(value.numberOrMark)
        }
        Ok(view(preparedForm, mrn, eventIndex, sealIndex, mode))
    }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, sealIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        val seals = request.userAnswers.get(SealsQuery(eventIndex)).getOrElse(Seq.empty)

        form(sealIndex, seals)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, eventIndex, sealIndex, mode))),
            value =>
              for {
                updatedAnswers <- Future.fromTry(request.userAnswers.set(SealIdentityPage(eventIndex, sealIndex), SealDomain(value)))
                _              <- sessionRepository.set(updatedAnswers)
              } yield Redirect(navigator.nextPage(SealIdentityPage(eventIndex, sealIndex), mode, updatedAnswers))
          )
    }
}
