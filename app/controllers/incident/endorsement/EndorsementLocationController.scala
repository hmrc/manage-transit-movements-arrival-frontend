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

package controllers.incident.endorsement

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.incident.EndorsementLocationFormProvider
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.endorsement.{EndorsementCountryPage, EndorsementLocationPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.endorsement.EndorsementLocationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class EndorsementLocationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  formProvider: EndorsementLocationFormProvider,
  actions: Actions,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: EndorsementLocationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(EndorsementCountryPage(index))) {
        implicit request =>
          val country = request.arg
          val form    = formProvider("incident.endorsement.location", country.description)
          val preparedForm = request.userAnswers.get(EndorsementLocationPage(index)) match {
            case None        => form
            case Some(value) => form.fill(value)
          }
          Ok(view(preparedForm, mrn, country.description, mode, index))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(EndorsementCountryPage(index)))
      .async {
        implicit request =>
          val country = request.arg
          val form    = formProvider("incident.endorsement.location", country.description)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, country.description, mode, index))),
              value => {
                implicit lazy val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                EndorsementLocationPage(index).writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
}
