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

package controllers.incident.equipment.seal

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.incident.SealIdentificationFormProvider
import models.requests.DataRequest
import models.{Index, Mode, MovementReferenceNumber, RichOptionJsArray}
import navigation.{SealNavigatorProvider, UserAnswersNavigator}
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
  val sessionRepository: SessionRepository,
  navigatorProvider: SealNavigatorProvider,
  formProvider: SealIdentificationFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: SealIdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix = "incident.equipment.seal.sealIdentificationNumber"

  private def form(prefix: String, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index, args: Seq[String] = Seq.empty)(implicit
    request: DataRequest[_]
  ): Form[String] =
    formProvider(
      prefix,
      otherSealIdentificationNumbers(incidentIndex, equipmentIndex, sealIndex),
      args: _*
    )

  private def otherSealIdentificationNumbers(incidentIndex: Index, equipmentIndex: Index, sealIndex: Index)(implicit request: DataRequest[_]): Seq[String] = {
    val numberOfSeals = request.userAnswers.get(SealsSection(incidentIndex, equipmentIndex)).length
    (0 until numberOfSeals)
      .map(Index(_))
      .filterNot(_ == sealIndex)
      .map(SealIdentificationNumberPage(incidentIndex, equipmentIndex, _))
      .flatMap(request.userAnswers.get(_))
  }

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] =
    actions
      .requireData(mrn) {
        implicit request =>
          val preparedForm = request.userAnswers.get(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)) match {
            case None        => form(prefix, incidentIndex, equipmentIndex, sealIndex)
            case Some(value) => form(prefix, incidentIndex, equipmentIndex, sealIndex).fill(value)
          }
          Ok(view(preparedForm, mrn, mode, incidentIndex, equipmentIndex, sealIndex, prefix))
      }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): Action[AnyContent] = actions
    .requireData(mrn)
    .async {
      implicit request =>
        form(prefix, incidentIndex, equipmentIndex, sealIndex)
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, incidentIndex, equipmentIndex, sealIndex, prefix))),
            value => {
              val navigator: UserAnswersNavigator = navigatorProvider(mode, incidentIndex, equipmentIndex, sealIndex)
              SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex)
                .writeToUserAnswers(value)
                .writeToSession(sessionRepository)
                .navigateWith(navigator)
            }
          )
    }
}
