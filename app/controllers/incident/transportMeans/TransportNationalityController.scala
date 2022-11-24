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

package controllers.incident.transportMeans

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.NationalityFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.transportMeans.TransportNationalityPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.NationalitiesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.transportMeans.TransportNationalityView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TransportNationalityController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  formProvider: NationalityFormProvider,
  service: NationalitiesService,
  val controllerComponents: MessagesControllerComponents,
  view: TransportNationalityView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getNationalities().map {
        nationalityList =>
          val form = formProvider("incident.transportMeans.transportNationality", nationalityList)
          val preparedForm = request.userAnswers.get(TransportNationalityPage(incidentIndex)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, nationalityList.nationalities, mode, incidentIndex))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getNationalities().flatMap {
        nationalityList =>
          val form = formProvider("incident.transportMeans.transportNationality", nationalityList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, nationalityList.nationalities, mode, incidentIndex))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, incidentIndex)
                TransportNationalityPage(incidentIndex).writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
  }
}