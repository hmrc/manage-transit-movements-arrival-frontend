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

import com.google.inject.Inject
import controllers.actions.Actions
import models.{CheckMode, Index, MovementReferenceNumber, NormalMode}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.CheckEventAnswersViewModel
import views.html.events.CheckEventAnswersView

import scala.concurrent.ExecutionContext

class CheckEventAnswersController @Inject() (
  override val messagesApi: MessagesApi,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  countriesService: CountriesService,
  view: CheckEventAnswersView,
  viewModel: CheckEventAnswersViewModel
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, eventIndex: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      countriesService
        .getTransitCountries()
        .map {
          countryList =>
            val sections = viewModel(request.userAnswers, eventIndex, CheckMode, countryList)
            Ok(view(mrn, sections))
        }
  }

  def onSubmit(mrn: MovementReferenceNumber, eventIndex: Index): Action[AnyContent] = actions.requireData(mrn) {
    _ =>
      Redirect(controllers.events.routes.AddEventController.onPageLoad(mrn, NormalMode))
  }

}
