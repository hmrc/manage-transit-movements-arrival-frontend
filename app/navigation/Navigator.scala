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

import controllers.routes
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages._
import play.api.mvc.Call

class Navigator {

  val normalRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case MovementReferenceNumberPage =>
      ua => Some(routes.MovementReferenceNumberController.onPageLoad())
  }

  val checkRoutes: PartialFunction[Page, UserAnswers => Option[Call]] = {
    case MovementReferenceNumberPage =>
      ua => Some(routes.MovementReferenceNumberController.onPageLoad())
  }

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = {

    def lift(routes: PartialFunction[Page, UserAnswers => Option[Call]])(default: Call): Call =
      routes.lift(page) match {
        case None => default
        case Some(call) =>
          call(userAnswers) match {
            case Some(onwardRoute) => onwardRoute
            case None              => controllers.routes.SessionExpiredController.onPageLoad()
          }
      }

    mode match {
      case NormalMode =>
        lift(normalRoutes) {
          routes.MovementReferenceNumberController.onPageLoad()
        }
      case CheckMode =>
        lift(checkRoutes) {
          ???
        }
    }
  }

}
