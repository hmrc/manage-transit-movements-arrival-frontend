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

package navigation

import models._
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.identification.AuthorisationsDomain

import javax.inject.{Inject, Singleton}

@Singleton
class AuthorisationsNavigatorProviderImpl @Inject() () extends AuthorisationsNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    mode match {
      case NormalMode => new AuthorisationsNavigator(mode)
      case CheckMode  => new ArrivalNavigator(mode)
    }
}

trait AuthorisationsNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class AuthorisationsNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = AuthorisationsDomain

  implicit override val reader: UserAnswersReader[AuthorisationsDomain] =
    AuthorisationsDomain.userAnswersReader
}