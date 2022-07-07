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

package controllers.identification

import config.FrontendAppConfig
import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import derivable.DeriveNumberOfIdentificationAuthorisations
import forms.AddItemFormProvider
import javax.inject.Inject
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.IdentificationDetails
import pages.identification.AddAnotherAuthorisationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.identification.AddAnotherAuthorisationView
import utils.AddAuthorisationsHelper

import scala.concurrent.{ExecutionContext, Future}

class AddAnotherAuthorisationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @IdentificationDetails implicit val navigator: Navigator,
  actions: Actions,
  formProvider: AddItemFormProvider,
  val controllerComponents: MessagesControllerComponents,
  config: FrontendAppConfig,
  view: AddAnotherAuthorisationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix = "identification.addAnotherAuthorisation"

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val preparedForm = formProvider("identification.addAnotherAuthorisation", allowMoreItems)
      Ok(view(preparedForm, mrn, mode, authorisations, allowMoreItems))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      formProvider(prefix, allowMoreItems)
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, authorisations, allowMoreItems))),
          value => AddAnotherAuthorisationPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
        )
  }

  private def authorisations(mode: Mode)(implicit request: DataRequest[_]): Seq[ListItem] = {
    val addAuthorisationsHelper = new AddAuthorisationsHelper(prefix, request.userAnswers, mode)
    (0 to numberOfAuthorisations) flatMap {
      x => addAuthorisationsHelper.authorisationListItem(Index(x))
    }
  }

  private def allowMoreItems(implicit request: DataRequest[_]): Boolean =
    numberOfAuthorisations < config.maxIdentificationAuthorisations

  private def numberOfAuthorisations(implicit request: DataRequest[_]): Int =
    request.userAnswers.get(DeriveNumberOfIdentificationAuthorisations).getOrElse(0)
}
