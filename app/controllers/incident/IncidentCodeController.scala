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

package controllers.incident

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.incident.IncidentCode
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.IncidentCodePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.IncidentCodeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.IncidentCodeView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IncidentCodeController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  incidentCodeService: IncidentCodeService,
  val controllerComponents: MessagesControllerComponents,
  view: IncidentCodeView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      incidentCodeService.getIncidentCodes().map {
        incidentCodes =>
          val preparedForm = request.userAnswers.get(IncidentCodePage(index)) match {
            case None        => form(incidentCodes)
            case Some(value) => form(incidentCodes).fill(value)
          }
          Ok(view(preparedForm, mrn, incidentCodes, mode, index))
      }
  }

  private def form(incidentCodes: Seq[IncidentCode]): Form[IncidentCode] =
    formProvider[IncidentCode]("incident.incidentCode", incidentCodes)

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      incidentCodeService.getIncidentCodes().flatMap {
        incidentCodes =>
          form(incidentCodes)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, incidentCodes, mode, index))),
              value => {
                implicit lazy val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                IncidentCodePage(index).writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
  }
}
