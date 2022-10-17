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

package controllers.locationOfGoods

import controllers.actions._
import controllers.{NavigatorOps, SettableOps, SettableOpsRunner}
import forms.PostalCodeFormProvider
import models.{Mode, MovementReferenceNumber}
import navigation.{LocationOfGoodsNavigatorProvider, UserAnswersNavigator}
import pages.locationOfGoods.AddressPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.locationOfGoods.AddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  navigatorProvider: LocationOfGoodsNavigatorProvider,
  actions: Actions,
  formProvider: PostalCodeFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: AddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val prefix: String = "locationOfGoods.address"

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      countriesService.getAddressPostcodeBasedCountries.map {
        countryList =>
          val form = formProvider(prefix, countryList)
          val preparedForm = request.userAnswers.get(AddressPage) match {
            case None        => form
            case Some(value) => form.fill(value)
          }

          Ok(view(preparedForm, mrn, mode, countryList.countries))
      }
  }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions.requireData(mrn).async {
    implicit request =>
      countriesService.getAddressPostcodeBasedCountries.flatMap {
        countryList =>
          val form = formProvider(prefix, countryList)
          form
            .bindFromRequest()
            .fold(
              formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, countryList.countries))),
              value => {
                implicit val navigator: UserAnswersNavigator = navigatorProvider(mode)
                AddressPage.writeToUserAnswers(value).writeToSession().navigate()
              }
            )
      }
  }
}
