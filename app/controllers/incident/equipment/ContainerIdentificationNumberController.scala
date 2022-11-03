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

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.incident.ContainerIdentificationFormProvider
import models.{Index, Mode, MovementReferenceNumber, RichJsArray}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.equipment.ContainerIdentificationNumberPage
import pages.sections.incident.IncidentsSection
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsPath.\
import play.api.libs.json.__
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.equipment.ContainerIdentificationNumberView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ContainerIdentificationNumberController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  formProvider: ContainerIdentificationFormProvider,
  actions: Actions,
  val controllerComponents: MessagesControllerComponents,
  view: ContainerIdentificationNumberView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider("incident.equipment.containerIdentificationNumber")

  private def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn) {
    implicit request =>
      val containerIdentificationNumbers = request.userAnswers.get(IncidentsSection)
      .map(_.pickValuesByPath[String]( __ \ "equipment" \ "containerIdentificationNumber"))
        .getOrElse(Nil)
        .zipWithIndex
        .filterNot {
          case (_,i) => i == index.position
        }.flatMap(_._1)


      val preparedForm = request.userAnswers.get(ContainerIdentificationNumberPage(index)) match {
        case None        => form
        case Some(value) => form.fill(value)
      }
      Ok(view(preparedForm, mrn, mode, index))
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      form
        .bindFromRequest()
        .fold(
          formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, index))),
          value => {
            implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
            ContainerIdentificationNumberPage(index).writeToUserAnswers(value).writeToSession().navigate()
          }
        )
  }
}
