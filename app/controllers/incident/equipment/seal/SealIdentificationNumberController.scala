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

package controllers.incident.equipment.seal

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.incident.SealIdentificationFormProvider
import models.requests.SpecificDataRequestProvider1
import models.{Index, Mode, MovementReferenceNumber, RichOptionJsArray}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.equipment.ContainerIdentificationNumberPage
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.sections.incident.SealsSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.equipment.seal.SealIdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SealIdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  formProvider: SealIdentificationFormProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: SealIdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private type Request = SpecificDataRequestProvider1[String]#SpecificDataRequest[_]

  private def containerIdentificationNumber(implicit request: Request): String = request.arg

  private def form(incidentIndex: Index, sealIndex: Index, containerIdentificationNumber: String)(implicit request: Request) =
    formProvider("incident.equipment.seal.sealIdentificationNumber", otherSealIdentificationNumbers(incidentIndex, sealIndex), containerIdentificationNumber)

  private def otherSealIdentificationNumbers(incidentIndex: Index, sealIndex: Index)(implicit request: Request): Seq[String] = {
    val numberOfSeals = request.userAnswers.get(SealsSection(incidentIndex)).length
    (0 until numberOfSeals)
      .map(Index(_))
      .filterNot(_ == sealIndex)
      .map(SealIdentificationNumberPage(incidentIndex, _))
      .flatMap(request.userAnswers.get(_))
  }

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, sealIndex: Index): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(ContainerIdentificationNumberPage(incidentIndex))) { // TODO - we need default content for the case where this question hasn't been answered
        implicit request =>
          val preparedForm = request.userAnswers.get(SealIdentificationNumberPage(incidentIndex, sealIndex)) match {
            case None        => form(incidentIndex, sealIndex, containerIdentificationNumber)
            case Some(value) => form(incidentIndex, sealIndex, containerIdentificationNumber).fill(value)
          }
          Ok(view(preparedForm, mrn, mode, incidentIndex, sealIndex, containerIdentificationNumber))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, sealIndex: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(ContainerIdentificationNumberPage(incidentIndex)))
    .async {
      implicit request =>
        form(incidentIndex, sealIndex, containerIdentificationNumber)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, incidentIndex, sealIndex, containerIdentificationNumber))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, incidentIndex)
              SealIdentificationNumberPage(incidentIndex, sealIndex).writeToUserAnswers(value).writeToSession().navigate()
            }
          )
    }
}
