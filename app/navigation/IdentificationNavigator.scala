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

package navigation

import controllers.identification.{routes => idRoutes}
import controllers.identification.authorisation.{routes => idAuthRoutes}
import derivable.DeriveNumberOfIdentificationAuthorisations
import javax.inject.{Inject, Singleton}
import models._
import pages.QuestionPage
import pages.identification._
import pages.identification.authorisation._
import play.api.mvc.Call

@Singleton
class IdentificationNavigator @Inject() () extends Navigator {

  override val normalRoutes: RouteMapping = routes(NormalMode)

  override val checkRoutes: RouteMapping = routes(CheckMode)

  override def routes(mode: Mode): RouteMapping = {
    case MovementReferenceNumberPage         => ua => Some(idRoutes.ArrivalDateController.onPageLoad(ua.mrn, mode))
    case ArrivalDatePage                     => ua => Some(idRoutes.IsSimplifiedProcedureController.onPageLoad(ua.mrn, mode))
    case IsSimplifiedProcedurePage           => ua => addAuthorisationRoute(IsSimplifiedProcedurePage, ua, mode)
    case AuthorisationTypePage(index)        => ua => Some(idAuthRoutes.AuthorisationReferenceNumberController.onPageLoad(ua.mrn, index, mode))
    case AuthorisationReferenceNumberPage(_) => ua => Some(idAuthRoutes.AddAnotherAuthorisationController.onPageLoad(ua.mrn, mode))
    case AddAnotherAuthorisationPage         => ua => addAuthorisationRoute(AddAnotherAuthorisationPage, ua, mode)
  }

  private def addAuthorisationRoute(page: QuestionPage[Boolean], ua: UserAnswers, mode: Mode): Option[Call] =
    yesNoRoute(ua, page)(
      yesCall = {
        val count = ua.get(DeriveNumberOfIdentificationAuthorisations).getOrElse(0)
        idAuthRoutes.AuthorisationTypeController.onPageLoad(ua.mrn, Index(count), mode)
      }
    )(
      noCall = idRoutes.IdentificationNumberController.onPageLoad(ua.mrn, mode)
    )

}
