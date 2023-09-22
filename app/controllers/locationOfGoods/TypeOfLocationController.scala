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
import models.reference.TypeOfLocation
import models.{Mode, MovementReferenceNumber}
import navigation.{ArrivalNavigatorProvider, UserAnswersNavigator}
import pages.locationOfGoods.TypeOfLocationPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.IncidentCodeService
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
  val controllerComponents: MessagesControllerComponents,
  view: TypeOfLocationView,
  service: IncidentCodeService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(typesOfLocation: Seq[TypeOfLocation]): Form[TypeOfLocation] =
    formProvider[TypeOfLocation]("locationOfGoods.typeOfLocation", typesOfLocation)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getTypesOfLocation().map {
        typesOfLocation =>
          val preparedForm = request.userAnswers.get(TypeOfLocationPage) match {
            case None        => form(typesOfLocation)
            case Some(value) => form(typesOfLocation).fill(value)
          }

          Ok(view(preparedForm, mrn, typesOfLocation, mode))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      service.getTypesOfLocation().flatMap {
        typesOfLocation =>
          form(typesOfLocation)
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, typesOfLocation, mode))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
                TypeOfLocationPage.writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
  }
}
