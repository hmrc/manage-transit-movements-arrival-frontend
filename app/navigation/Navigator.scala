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

import controllers.events.{routes => eventRoutes}
import controllers.routes
import derivable.DeriveNumberOfEvents
import models.GoodsLocation._
import models.{CheckMode, Index, Mode, NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

class Navigator {

  val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case MovementReferenceNumberPage =>
      ua => Some(routes.GoodsLocationController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case GoodsLocationPage =>
      ua => Some(routes.CustomsSubPlaceController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case AuthorisedLocationPage =>
      ua => Some(routes.ConsigneeNameController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case ConsigneeNamePage =>
      ua => Some(routes.ConsigneeEoriNumberController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case ConsigneeEoriNumberPage =>
      ua => Some(routes.ConsigneeAddressController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    // go to simplified customs office mode if simplified route is being pursued.
    case ConsigneeAddressPage =>
      ua => Some(routes.CustomsOfficeSimplifiedController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case SimplifiedCustomsOfficePage =>
      ua => Some(routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case CustomsOfficePage =>
      customsOffice(NormalMode)
    case CustomsSubPlacePage =>
      ua => Some(routes.CustomsOfficeController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case TraderNamePage =>
      ua => Some(routes.TraderEoriController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case TraderAddressPage =>
      ua => Some(routes.IsTraderAddressPlaceOfNotificationController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case TraderEoriPage =>
      ua => Some(routes.TraderAddressController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case IsTraderAddressPlaceOfNotificationPage =>
      isTraderAddressPlaceOfNotificationRoute
    case PlaceOfNotificationPage =>
      ua => Some(routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, NormalMode))
    case IncidentOnRoutePage =>
      incidentOnRoute
    case UpdateRejectedMRNPage =>
      ua => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
  }

  val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case GoodsLocationPage                      => goodsLocationCheckRoute
    case AuthorisedLocationPage                 => authorisedLocationRoute(CheckMode)
    case CustomsSubPlacePage                    => customsSubPlaceRoute(CheckMode)
    case ConsigneeNamePage                      => consigneeNameRoute(CheckMode)
    case ConsigneeEoriNumberPage                => consigneeEoriNumberRoute(CheckMode)
    case ConsigneeAddressPage                   => consigneeAddressRoute(CheckMode)
    case TraderAddressPage                      => traderAddressRoute(CheckMode)
    case IsTraderAddressPlaceOfNotificationPage => isTraderAddressPlaceOfNotificationCheckRoute
    case CustomsOfficePage                      => customsOffice(CheckMode)
    case TraderNamePage                         => traderNameRoute(CheckMode)
    case TraderEoriPage                         => traderEoriRoute(CheckMode)
    case IncidentOnRoutePage                    => incidentOnRoute
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes.lift(page) match {
        case None => routes.MovementReferenceNumberController.onPageLoad()
        case Some(call) =>
          call(userAnswers) match {
            case Some(onwardRoute) => onwardRoute
            case None              => routes.SessionExpiredController.onPageLoad()
          }
      }
    case CheckMode =>
      checkRoutes.lift(page) match {
        case None => routes.CheckYourAnswersController.onPageLoad(userAnswers.movementReferenceNumber)
        case Some(call) =>
          call(userAnswers) match {
            case Some(onwardRoute) => onwardRoute
            case None              => routes.SessionExpiredController.onPageLoad()
          }
      }
  }

  private def traderAddressRoute(mode: Mode)(ua: UserAnswers) =
    (ua.get(IsTraderAddressPlaceOfNotificationPage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case (None, _)            => Some(routes.IsTraderAddressPlaceOfNotificationController.onPageLoad(ua.movementReferenceNumber, mode))
      case _                    => None
    }

  private def consigneeEoriNumberRoute(mode: Mode)(ua: UserAnswers) =
    (mode, ua.get(ConsigneeAddressPage)) match {
      case (CheckMode, Some(_)) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.ConsigneeAddressController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def consigneeNameRoute(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(ConsigneeAddressPage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.ConsigneeEoriNumberController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def consigneeAddressRoute(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(CustomsOfficePage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.CustomsOfficeSimplifiedController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def customsOffice(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(GoodsLocationPage), ua.get(TraderNamePage), mode) match {
      case (Some(BorderForceOffice), Some(_), _)               => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case (Some(BorderForceOffice), None, _)                  => Some(routes.TraderNameController.onPageLoad(ua.movementReferenceNumber, mode))
      case (Some(AuthorisedConsigneesLocation), _, NormalMode) => Some(routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, mode))
      case (Some(AuthorisedConsigneesLocation), _, _)          => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                                                   => Some(routes.SessionExpiredController.onPageLoad())
    }

  private def traderNameRoute(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(TraderEoriPage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.TraderEoriController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def traderEoriRoute(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(TraderAddressPage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.TraderAddressController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def customsSubPlaceRoute(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(CustomsOfficePage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.CustomsOfficeController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def authorisedLocationRoute(mode: Mode)(ua: UserAnswers): Option[Call] =
    (ua.get(ConsigneeNamePage), mode) match {
      case (Some(_), CheckMode) => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                    => Some(routes.ConsigneeNameController.onPageLoad(ua.movementReferenceNumber, mode))
    }

  private def incidentOnRoute(ua: UserAnswers): Option[Call] =
    (ua.get(IncidentOnRoutePage), ua.get(DeriveNumberOfEvents)) match {
      case (Some(true), None | Some(0)) => Some(eventRoutes.EventCountryController.onPageLoad(ua.movementReferenceNumber, Index(0), NormalMode))
      case (Some(true), Some(_))        => Some(eventRoutes.AddEventController.onPageLoad(ua.movementReferenceNumber, NormalMode))
      case (Some(false), _)             => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                            => None
    }

  private def goodsLocationCheckRoute(ua: UserAnswers): Option[Call] =
    // TODO: Get the requirements for this sorted out. AuthorisedLocationPage is not actually being used here
    (ua.get(GoodsLocationPage), ua.get(AuthorisedLocationPage), ua.get(CustomsSubPlacePage)) match {
      case (Some(BorderForceOffice), _, None)         => Some(routes.CustomsSubPlaceController.onPageLoad(ua.movementReferenceNumber, CheckMode))
      case (Some(AuthorisedConsigneesLocation), _, _) => Some(routes.UseDifferentServiceController.onPageLoad(ua.movementReferenceNumber))
      case _ =>
        Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber)) // TODO: This branch is ill defined and needs to be fixed
    }

  private def isTraderAddressPlaceOfNotificationRoute(ua: UserAnswers): Option[Call] =
    ua.get(IsTraderAddressPlaceOfNotificationPage) match {
      case Some(true)  => Some(routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, NormalMode))
      case Some(false) => Some(routes.PlaceOfNotificationController.onPageLoad(ua.movementReferenceNumber, NormalMode))
      case _           => None
    }

  private def isTraderAddressPlaceOfNotificationCheckRoute(ua: UserAnswers): Option[Call] =
    (ua.get(IsTraderAddressPlaceOfNotificationPage), ua.get(PlaceOfNotificationPage)) match {
      case (Some(false), None) => Some(routes.PlaceOfNotificationController.onPageLoad(ua.movementReferenceNumber, CheckMode))
      case (Some(_), _)        => Some(routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                   => None
    }

}
