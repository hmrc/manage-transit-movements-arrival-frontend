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

package controllers.incident.equipment

import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import controllers.actions._
import forms.YesNoFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.equipment.{AddSealsYesNoPage, ContainerIdentificationNumberPage}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.equipment.AddSealsYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddSealsYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  actions: Actions,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: AddSealsYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def containerIdentificationNumber(implicit request: Request): String = request.arg

  private def form(containerIdentificationNumber: String) =
    formProvider("incident.equipment.addSealsYesNo", containerIdentificationNumber)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(ContainerIdentificationNumberPage(index))) {
        implicit request =>
          val preparedForm = request.userAnswers.get(AddSealsYesNoPage(index)) match {
            case None        => form(containerIdentificationNumber)
            case Some(value) => form(containerIdentificationNumber).fill(value)
          }

          Ok(view(preparedForm, containerIdentificationNumber, mrn, mode, index))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(ContainerIdentificationNumberPage(index)))
    .async {
      implicit request =>
        form(containerIdentificationNumber)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, containerIdentificationNumber, mrn, mode, index))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
              AddSealsYesNoPage(index).writeToUserAnswers(value).writeToSession().navigate()
            }
          )
    }
}
