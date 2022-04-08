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

package controllers.events

import controllers.actions._
import forms.events.EventCountryFormProvider
import javax.inject.Inject
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.EventCountryPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.events.EventCountryView

import scala.concurrent.{ExecutionContext, Future}

class EventCountryController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  navigator: Navigator,
  actions: Actions,
  formProvider: EventCountryFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: EventCountryView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        countriesService.getTransitCountries() map {
          countryList =>
            val form = formProvider(countryList)

            val preparedForm = request.userAnswers
              .get(EventCountryPage(eventIndex))
              .flatMap(countryList.getCountry)
              .map(form.fill)
              .getOrElse(form)

            Ok(view(preparedForm, countryList.countries, mrn, mode, eventIndex))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] =
    actions.requireData(mrn).async {
      implicit request =>
        countriesService.getTransitCountries() flatMap {
          countryList =>
            formProvider(countryList)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, countryList.countries, mrn, mode, eventIndex))),
                value =>
                  for {
                    updatedAnswers <- Future.fromTry(request.userAnswers.set(EventCountryPage(eventIndex), value.code))
                    _              <- sessionRepository.set(updatedAnswers)
                  } yield Redirect(navigator.nextPage(EventCountryPage(eventIndex), mode, updatedAnswers))
              )
        }
    }
}
