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
import forms.SelectableFormProvider.CountryFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.IncidentCountryPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.IncidentCountryView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IncidentCountryController @Inject() (
  override val messagesApi: MessagesApi,
  val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  formProvider: CountryFormProvider,
  service: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: IncidentCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getTransitCountries().map {
        countryList =>
          val form = formProvider("incident.incidentCountry", countryList)
          val preparedForm = request.userAnswers.get(IncidentCountryPage(index)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, countryList.values, mode, index))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getTransitCountries().flatMap {
        countryList =>
          val form = formProvider("incident.incidentCountry", countryList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, countryList.values, mode, index))),
              value => {
                val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                IncidentCountryPage(index).writeToUserAnswers(value).writeToSession(sessionRepository).navigateWith(navigator)
              }
            )
      }
  }
}
