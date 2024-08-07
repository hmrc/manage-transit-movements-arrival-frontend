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

package controllers.incident.transportMeans

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.reference.Identification
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.transportMeans.IdentificationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.ReferenceDataDynamicRadioService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.transportMeans.IdentificationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IdentificationController @Inject() (
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  incidentCodeService: ReferenceDataDynamicRadioService,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: IdentificationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(identification: Seq[Identification]): Form[Identification] =
    formProvider[Identification]("incident.transportMeans.identification", identification)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      incidentCodeService.getTransportIdentifications().map {
        incidentIdentifications =>
          val preparedForm = request.userAnswers.get(IdentificationPage(incidentIndex)) match {
            case None        => form(incidentIdentifications)
            case Some(value) => form(incidentIdentifications).fill(value)
          }

          Ok(view(preparedForm, mrn, incidentIdentifications, mode, incidentIndex))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      incidentCodeService.getTransportIdentifications().flatMap {
        incidentIdentifications =>
          form(incidentIdentifications)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, incidentIdentifications, mode, incidentIndex))),
              value => {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, incidentIndex)
                IdentificationPage(incidentIndex).writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
              }
            )
      }
  }
}
