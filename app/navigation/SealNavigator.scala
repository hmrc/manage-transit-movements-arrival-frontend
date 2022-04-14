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
import controllers.events.{routes => eventRoutes}
import derivable.DeriveNumberOfSeals
import models.{CheckMode, Index, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.events.seals._
import play.api.mvc.Call

class SealNavigator extends Navigator {

  override val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case HaveSealsChangedPage(eventIndex) => haveSealsChanged(eventIndex, NormalMode)
    case SealIdentityPage(eventIndex, _) =>
      ua => Some(sealRoutes.AddSealController.onPageLoad(ua.movementReferenceNumber, eventIndex, NormalMode))
    case AddSealPage(eventIndex)           => addSeal(eventIndex, NormalMode)
    case ConfirmRemoveSealPage(eventIndex) => removeSeal(eventIndex, NormalMode)
  }

  override val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case HaveSealsChangedPage(index) => haveSealsChanged(index, CheckMode)
    case SealIdentityPage(index, _) =>
      ua => Some(sealRoutes.AddSealController.onPageLoad(ua.movementReferenceNumber, index, CheckMode))
    case AddSealPage(eventIndex)           => addSeal(eventIndex, CheckMode)
    case ConfirmRemoveSealPage(eventIndex) => removeSeal(eventIndex, CheckMode)
  }

  private def haveSealsChanged(eventIndex: Index, mode: Mode)(userAnswers: UserAnswers): Option[Call] =
    userAnswers.get(HaveSealsChangedPage(eventIndex)).map {
      case true =>
        if (userAnswers.get(SealIdentityPage(eventIndex, Index(0))).isDefined) { //todo: hardcoded 0?
          sealRoutes.AddSealController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex, mode)
        } else {
          val sealCount = userAnswers.get(DeriveNumberOfSeals(eventIndex)).getOrElse(0)
          val sealIndex = Index(sealCount)
          sealRoutes.SealIdentityController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex, sealIndex, mode)
        }
      case false => eventRoutes.CheckEventAnswersController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex)
    }

  private def addSeal(eventIndex: Index, mode: Mode)(userAnswers: UserAnswers): Option[Call] =
    userAnswers.get(AddSealPage(eventIndex)).map {
      case true =>
        val sealCount = userAnswers.get(DeriveNumberOfSeals(eventIndex)).getOrElse(0)
        val sealIndex = Index(sealCount)
        sealRoutes.SealIdentityController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex, sealIndex, mode)
      case false =>
        eventRoutes.CheckEventAnswersController.onPageLoad(userAnswers.movementReferenceNumber, eventIndex)
    }

  private def removeSeal(eventIndex: Index, mode: Mode)(ua: UserAnswers): Option[Call] =
    ua.get(DeriveNumberOfSeals(eventIndex)) match {
      case None | Some(0) => Some(sealRoutes.HaveSealsChangedController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
      case _              => Some(sealRoutes.AddSealController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
    }
}
