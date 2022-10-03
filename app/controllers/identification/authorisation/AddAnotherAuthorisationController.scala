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

package controllers.identification.authorisation

import config.FrontendAppConfig
import controllers.actions._
import controllers.identification.authorisation.{routes => authRoutes}
import forms.AddItemFormProvider
import models.journeyDomain.identification.AuthorisationDomain
import models.requests.DataRequest
import models.{Index, MovementReferenceNumber, NormalMode}
import navigation.Navigator
import navigation.annotations.IdentificationDetails
import pages.sections.{AuthorisationSection, AuthorisationsSection}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewModels.ListItem
import viewModels.identification.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider
import views.html.identification.authorisation.AddAnotherAuthorisationView

import javax.inject.Inject

class AddAnotherAuthorisationController @Inject() (
  override val messagesApi: MessagesApi,
  @IdentificationDetails navigator: Navigator,
  actions: Actions,
  removeInProgressAuthorisations: RemoveInProgressActionProvider,
  formProvider: AddItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  viewModelProvider: AddAnotherAuthorisationViewModelProvider,
  view: AddAnotherAuthorisationView
) extends FrontendBaseController
    with I18nSupport {

  private def form(allowMoreAuthorisations: Boolean): Form[Boolean] =
    formProvider("identification.authorisation.addAnotherAuthorisation", allowMoreAuthorisations)

  def onPageLoad(mrn: MovementReferenceNumber): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(removeInProgressAuthorisations[AuthorisationDomain](AuthorisationsSection, AuthorisationSection)) {
      implicit request =>
        val (authorisations, numberOfAuthorisations, allowMoreAuthorisations) = viewData
        numberOfAuthorisations match {
          case 0 => Redirect(controllers.identification.routes.IsSimplifiedProcedureController.onPageLoad(mrn, NormalMode))
          case _ => Ok(view(form(allowMoreAuthorisations), mrn, authorisations, allowMoreAuthorisations))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      lazy val (authorisations, numberOfAuthorisations, allowMoreAuthorisations) = viewData
      form(allowMoreAuthorisations)
        .bindFromRequest()
        .fold(
          formWithErrors => BadRequest(view(formWithErrors, mrn, authorisations, allowMoreAuthorisations)),
          {
            case true  => Redirect(authRoutes.AuthorisationTypeController.onPageLoad(mrn, Index(numberOfAuthorisations), NormalMode))
            case false => Redirect(navigator.nextPage(request.userAnswers, NormalMode))
          }
        )
  }

  private def viewData(implicit request: DataRequest[_]): (Seq[ListItem], Int, Boolean) = {
    val authorisations         = viewModelProvider.apply(request.userAnswers).listItems
    val numberOfAuthorisations = authorisations.length
    (authorisations, numberOfAuthorisations, numberOfAuthorisations < config.maxIdentificationAuthorisations)
  }
}
