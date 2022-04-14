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

package controllers.events.transhipments

import config.annotations.Container
import controllers.actions.Actions
import forms.events.transhipments.TransportNationalityFormProvider

import javax.inject.Inject
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import pages.events.transhipments.TransportNationalityPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.events.transhipments.TransportNationalityView

import scala.concurrent.{ExecutionContext, Future}

class TransportNationalityController @Inject() (
  override val messagesApi: MessagesApi,
  sessionRepository: SessionRepository,
  @Container navigator: Navigator,
  actions: Actions,
  formProvider: TransportNationalityFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: TransportNationalityView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      countriesService.getCountries() map {
        countryList =>
          val form = formProvider(countryList)

          val preparedForm = request.userAnswers
            .get(TransportNationalityPage(eventIndex))
            .flatMap(countryList.getCountry)
            .map(form.fill)
            .getOrElse(form)

          Ok(view(preparedForm, countryList.countries, mrn, eventIndex, mode))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      countriesService.getCountries() flatMap {
        countryList =>
          val form = formProvider(countryList)

          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, countryList.countries, mrn, eventIndex, mode))),
              value =>
                for {
                  updatedAnswers <- Future.fromTry(request.userAnswers.set(TransportNationalityPage(eventIndex), value.code))
                  _              <- sessionRepository.set(updatedAnswers)
                } yield Redirect(navigator.nextPage(TransportNationalityPage(eventIndex), mode, updatedAnswers))
            )
      }
  }
}
