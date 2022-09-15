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
import forms.InternationalAddressFormProvider
import models.requests.DataRequest
import models.{CountryList, InternationalAddress, Mode, MovementReferenceNumber}
import navigation.Navigator
import navigation.annotations.LocationOfGoods
import pages.locationOfGoods.InternationalAddressPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import services.CountriesService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.locationOfGoods.InternationalAddressView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class InternationalAddressController @Inject() (
  override val messagesApi: MessagesApi,
  implicit val sessionRepository: SessionRepository,
  @LocationOfGoods implicit val navigator: Navigator,
  actions: Actions,
  formProvider: InternationalAddressFormProvider,
  countriesService: CountriesService,
  val controllerComponents: MessagesControllerComponents,
  view: InternationalAddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private def form(countryList: CountryList)(implicit request: DataRequest[AnyContent]): Form[InternationalAddress] =
    formProvider("locationOfGoods.internationalAddress", countryList)

  def onPageLoad(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .async {
      implicit request: DataRequest[AnyContent] =>
        countriesService.getCountries().map {
          countryList =>
            val preparedForm = request.userAnswers.get(InternationalAddressPage) match {
              case None        => form(countryList)
              case Some(value) => form(countryList).fill(value)
            }

            Ok(view(preparedForm, mrn, mode, countryList.countries))
        }
    }

  def onSubmit(mrn: MovementReferenceNumber, mode: Mode): Action[AnyContent] = actions
    .requireData(mrn)
    .async {
      implicit request =>
        countriesService.getCountries().flatMap {
          countryList =>
            form(countryList)
              .bindFromRequest()
              .fold(
                formWithErrors => Future.successful(BadRequest(view(formWithErrors, mrn, mode, countryList.countries))),
                value => InternationalAddressPage.writeToUserAnswers(value).writeToSession().navigateWith(mode)
              )
        }
    }
}