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

package controllers.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.EnumerableFormProvider
import models.identification.ProcedureType.Simplified
import models.locationOfGoods.TypeOfLocation
import models.{Mode, MovementReferenceNumber}
import navigation.{ArrivalNavigatorProvider, UserAnswersNavigator}
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.TypeOfLocationPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.locationOfGoods.TypeOfLocationView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class TypeOfLocationController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: ArrivalNavigatorProvider,
  actions: Actions,
  formProvider: EnumerableFormProvider,
  getMandatoryPage: SpecificDataRequiredActionProvider,
  val controllerComponents: MessagesControllerComponents,
  view: TypeOfLocationView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form = formProvider[TypeOfLocation]("locationOfGoods.typeOfLocation")

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(IsSimplifiedProcedurePage)) {
      implicit request =>
        val typeOfLocationValues =
          if (request.arg == Simplified) TypeOfLocation.values else TypeOfLocation.normalProcedureValues
        val preparedForm = request.userAnswers.get(TypeOfLocationPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mrn, typeOfLocationValues, mode))
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .andThen(getMandatoryPage(IsSimplifiedProcedurePage))
    .async {
      implicit request =>
        val typeOfLocationValues =
          if (request.arg == Simplified) TypeOfLocation.values else TypeOfLocation.normalProcedureValues
        form
          .bindFromRequest()
          .fold(
            formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, typeOfLocationValues, mode))),
            value => {
              implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
              TypeOfLocationPage.writeToUserAnswers(value).writeToSession().navigate()
            }
          )
    }
}
