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
import play.api.data.Form
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
  navigatorProvider: IncidentNavigatorProvider, // TODO - create new nav.
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

  private def form(incidentIndex: Index, sealIndex: Index, equipmentIndex: Index)(implicit request: Request): Form[String] =
    formProvider(
      "incident.equipment.seal.sealIdentificationNumber",
      otherSealIdentificationNumbers(incidentIndex, equipmentIndex, sealIndex),
      containerIdentificationNumber
    )

  private def otherSealIdentificationNumbers(incidentIndex: Index, equipmentIndex: Index, sealIndex: Index)(implicit request: Request): Seq[String] = {
    val numberOfSeals = request.userAnswers.get(SealsSection(incidentIndex, equipmentIndex)).length
    (0 until numberOfSeals)
      .map(Index(_))
      .filterNot(_ == sealIndex)
      .map(SealIdentificationNumberPage(incidentIndex, equipmentIndex, _))
      .flatMap(request.userAnswers.get(_))
  }

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] =
    actions
      .requireData(mrn)
      .andThen(getMandatoryPage(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex))) { // TODO - we need default content for the case where this question hasn't been answered
        implicit request =>
          val preparedForm = request.userAnswers.get(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)) match {
            case None        => form(incidentIndex, equipmentIndex, sealIndex)
            case Some(value) => form(incidentIndex, equipmentIndex, sealIndex).fill(value)
          }
          Ok(view(preparedForm, mrn, mode, incidentIndex, equipmentIndex, sealIndex, containerIdentificationNumber))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)))
    .async {
      implicit request =>
        form(incidentIndex, equipmentIndex, sealIndex)
          .bindFromRequest()
          .fold(
            formWithErrors =>
              Future.successful(BadRequest(view(formWithErrors, mrn, mode, incidentIndex, equipmentIndex, sealIndex, containerIdentificationNumber))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, incidentIndex)
              SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex).writeToUserAnswers(value).writeToSession().navigate()
            }
          )
    }
}
