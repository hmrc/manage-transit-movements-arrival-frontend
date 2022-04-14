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

import controllers.events.seals.{routes => sealRoutes}
import controllers.events.transhipments.{routes => transhipmentRoutes}
import controllers.events.{routes => eventRoutes}
import derivable.{DeriveNumberOfContainers, DeriveNumberOfEvents}
import models.TranshipmentType.{DifferentContainer, DifferentContainerAndVehicle}
import models.{CheckMode, Index, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.events._
import pages.events.transhipments.TranshipmentTypePage
import play.api.mvc.Call

class EventNavigator extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case EventCountryPage(eventIndex) =>
      ua => Some(eventRoutes.EventPlaceController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case EventPlacePage(eventIndex) =>
      ua => Some(eventRoutes.EventReportedController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case EventReportedPage(eventIndex) =>
      ua => Some(eventRoutes.IsTranshipmentController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case IsTranshipmentPage(eventIndex) => isTranshipmentRoute(eventIndex)
    case IncidentInformationPage(eventIndex) =>
      ua => Some(sealRoutes.HaveSealsChangedController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case AddEventPage                       => addEventRoute
    case ConfirmRemoveEventPage(eventIndex) => confirmRemoveEventRoute(eventIndex, NormalMode)
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case EventCountryPage(index) =>
      ua => Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, index))
    case EventPlacePage(index) =>
      ua => Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, index))
    case EventReportedPage(index)  => eventReportedCheckRoute(index)
    case IsTranshipmentPage(index) => isTranshipmentCheckRoute(index)
    case IncidentInformationPage(index) =>
      ua => Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, index))
    case AddEventPage                  => addEventRoute
    case ConfirmRemoveEventPage(index) => confirmRemoveEventRoute(index, CheckMode)
  }

  private def isTranshipmentRoute(eventIndex: Index)(ua: UserAnswers): Option[Call] =
    (ua.get(EventReportedPage(eventIndex)), ua.get(IsTranshipmentPage(eventIndex))) match {
      case (_, Some(true))            => Some(transhipmentRoutes.TranshipmentTypeController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
      case (Some(false), Some(false)) => Some(eventRoutes.IncidentInformationController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
      case (Some(true), Some(false))  => Some(sealRoutes.HaveSealsChangedController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
      case _                          => None
    }

  private def isTranshipmentCheckRoute(eventIndex: Index)(ua: UserAnswers): Option[Call] =
    (
      ua.get(EventReportedPage(eventIndex)),
      ua.get(IsTranshipmentPage(eventIndex)),
      ua.get(IncidentInformationPage(eventIndex)),
      ua.get(TranshipmentTypePage(eventIndex)),
      ua.get(DeriveNumberOfContainers(eventIndex))
    ) match {
      case (Some(false), Some(false), None, _, _) =>
        Some(eventRoutes.IncidentInformationController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode))
      case (_, Some(true), _, None, _) => Some(transhipmentRoutes.TranshipmentTypeController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode))
      case (_, Some(true), _, Some(DifferentContainer) | Some(DifferentContainerAndVehicle), Some(0) | None) =>
        Some(transhipmentRoutes.TranshipmentTypeController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode))
      case _ => Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
    }

  private def eventReportedCheckRoute(eventIndex: Index)(userAnswers: UserAnswers): Option[Call] =
    (userAnswers.get(EventReportedPage(eventIndex)), userAnswers.get(IsTranshipmentPage(eventIndex))) match {
      case (Some(false), Some(false)) => Some(eventRoutes.IncidentInformationController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex, CheckMode))
      case _                          => Some(eventRoutes.CheckEventAnswersController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex))
    }

  private def addEventRoute(ua: UserAnswers): Option[Call] =
    (ua.get(AddEventPage), ua.get(DeriveNumberOfEvents)) match {
      case (Some(true), Some(eventIndex)) => Some(eventRoutes.EventCountryController.onPageLoad(ua.movementReferenceNumber, Index(eventIndex), NormalMode))
      case (Some(true), None)             => Some(eventRoutes.EventCountryController.onPageLoad(ua.movementReferenceNumber, Index(0), NormalMode))
      case (Some(false), _)               => Some(controllers.routes.CheckYourAnswersController.onPageLoad(ua.movementReferenceNumber))
      case _                              => None
    }

  private def confirmRemoveEventRoute(eventIndex: Index, mode: Mode)(ua: UserAnswers): Option[Call] = ua.get(DeriveNumberOfEvents) match {
    case Some(0) | None => Some(controllers.routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, mode))
    case _              => Some(eventRoutes.AddEventController.onPageLoad(ua.movementReferenceNumber, mode))
  }
}
