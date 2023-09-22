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

package controllers.incident.location

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.reference.QualifierOfIdentification
import models.{Index, Mode, MovementReferenceNumber}
import navigation.{IncidentNavigatorProvider, UserAnswersNavigator}
import pages.incident.location.QualifierOfIdentificationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.IncidentCodeService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.incident.location.QualifierOfIdentificationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class QualifierOfIdentificationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: IncidentNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: QualifierOfIdentificationView,
  service: IncidentCodeService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(qualifiers: Seq[QualifierOfIdentification]): Form[QualifierOfIdentification] =
    formProvider[QualifierOfIdentification]("incident.location.qualifierOfIdentification", qualifiers)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getIdentifications().map {
        qualifiers =>
          val preparedForm = request.userAnswers.get(QualifierOfIdentificationPage(index)) match {
            case None        => form(qualifiers)
            case Some(value) => form(qualifiers).fill(value)
          }

          Ok(view(preparedForm, mrn, qualifiers, mode, index))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode, index: Index): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getIdentifications().flatMap {
        qualifiers =>
          form(qualifiers)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, qualifiers, mode, index))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode, index)
                QualifierOfIdentificationPage(index).writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
  }
}
