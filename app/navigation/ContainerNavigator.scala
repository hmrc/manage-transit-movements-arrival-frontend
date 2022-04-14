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
import derivable.DeriveNumberOfContainers
import models.TranshipmentType.{DifferentContainer, DifferentContainerAndVehicle, DifferentVehicle}
import models.{CheckMode, Index, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.events.transhipments._
import play.api.mvc.Call

class ContainerNavigator extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TranshipmentTypePage(eventIndex) => transhipmentType(eventIndex)
    case ContainerNumberPage(eventIndex, _) =>
      ua => Some(transhipmentRoutes.AddContainerController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case TransportIdentityPage(eventIndex) =>
      ua => Some(transhipmentRoutes.TransportNationalityController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case TransportNationalityPage(eventIndex) =>
      ua => Some(sealRoutes.HaveSealsChangedController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case AddContainerPage(eventIndex)           => addContainer(eventIndex)
    case ConfirmRemoveContainerPage(eventIndex) => confirmRemoveContainerRoute(eventIndex, NormalMode)
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case TranshipmentTypePage(index) => transhipmentTypeCheckRoute(index)
    case ContainerNumberPage(index, _) =>
      ua => Some(transhipmentRoutes.AddContainerController.onPageLoad(ua.movementReferenceNumber, index, CheckMode))
    case TransportIdentityPage(index) => transportIdentity(index)
    case TransportNationalityPage(index) =>
      ua => Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, index))
    case AddContainerPage(index)           => addContainerCheckRoute(index)
    case ConfirmRemoveContainerPage(index) => confirmRemoveContainerRoute(index, CheckMode)
  }

  private def transhipmentType(eventIndex: Index)(ua: UserAnswers): Option[Call] = ua.get(TranshipmentTypePage(eventIndex)) map {
    case DifferentContainer | DifferentContainerAndVehicle =>
      ua.get(DeriveNumberOfContainers(eventIndex)) match {
        case Some(0) | None => transhipmentRoutes.ContainerNumberController.onPageLoad(ua.movementReferenceNumber, eventIndex, Index(0), NormalMode)
        case Some(_)        => transhipmentRoutes.AddContainerController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode)
      }
    case DifferentVehicle => transhipmentRoutes.TransportIdentityController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode)
  }

  private def transhipmentTypeCheckRoute(eventIndex: Index)(ua: UserAnswers): Option[Call] =
    (
      ua.get(TranshipmentTypePage(eventIndex)),
      ua.get(ContainerNumberPage(eventIndex, Index(0))), //todo: confirm the logic here and hard coded 0's
      ua.get(TransportIdentityPage(eventIndex)),
      ua.get(TransportNationalityPage(eventIndex))
    ) match {
      case (Some(DifferentContainer) | Some(DifferentContainerAndVehicle), None, _, _) =>
        Some(transhipmentRoutes.ContainerNumberController.onPageLoad(ua.movementReferenceNumber, eventIndex, Index(0), CheckMode))

      case (Some(DifferentVehicle), _, None, _) =>
        Some(transhipmentRoutes.TransportIdentityController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode))

      case (Some(DifferentContainerAndVehicle), Some(_), Some(_), Some(_)) =>
        Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))

      case (Some(DifferentContainerAndVehicle), Some(_), _, _) =>
        Some(transhipmentRoutes.AddContainerController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode))

      case _ =>
        Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
    }

  private def addContainer(eventIndex: Index)(ua: UserAnswers): Option[Call] = ua.get(AddContainerPage(eventIndex)) map {
    case true =>
      // TODO: Need to consolidate with same logic for initialisation of eventIndex in transhipmentType
      val nextContainerCount = ua.get(DeriveNumberOfContainers(eventIndex)).getOrElse(0)
      val nextContainerIndex = Index(nextContainerCount)
      transhipmentRoutes.ContainerNumberController.onPageLoad(ua.movementReferenceNumber, eventIndex, nextContainerIndex, NormalMode)
    case false =>
      ua.get(TranshipmentTypePage(eventIndex)) match {
        case Some(DifferentContainerAndVehicle) => transhipmentRoutes.TransportIdentityController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode)
        case _                                  => sealRoutes.HaveSealsChangedController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode)
      }
  }

  private def addContainerCheckRoute(eventIndex: Index)(ua: UserAnswers): Option[Call] = ua.get(AddContainerPage(eventIndex)) map {
    case true =>
      val nextContainerCount = ua.get(DeriveNumberOfContainers(eventIndex)).getOrElse(0)
      val nextContainerIndex = Index(nextContainerCount)
      transhipmentRoutes.ContainerNumberController.onPageLoad(ua.movementReferenceNumber, eventIndex, nextContainerIndex, CheckMode)
    case false =>
      (
        ua.get(TranshipmentTypePage(eventIndex)),
        ua.get(TransportIdentityPage(eventIndex))
      ) match {
        case (Some(DifferentContainerAndVehicle), None) =>
          transhipmentRoutes.TransportIdentityController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode)
        case _ => eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex)
      }
  }

  private def transportIdentity(eventIndex: Index)(ua: UserAnswers): Option[Call] =
    ua.get(TransportNationalityPage(eventIndex)) match {
      case Some(_) => Some(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
      case _       => Some(transhipmentRoutes.TransportNationalityController.onPageLoad(ua.movementReferenceNumber, eventIndex, CheckMode))
    }

  def confirmRemoveContainerRoute(eventIndex: Index, mode: Mode)(ua: UserAnswers): Option[Call] = ua.get(DeriveNumberOfContainers(eventIndex)) match {
    case Some(0) | None => Some(eventRoutes.IsTranshipmentController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
    case _              => Some(transhipmentRoutes.AddContainerController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
  }
}
