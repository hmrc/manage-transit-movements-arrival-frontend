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

import models._
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.identification.AuthorisationDomain

import javax.inject.{Inject, Singleton}

@Singleton
class AuthorisationNavigatorProviderImpl @Inject() () extends AuthorisationNavigatorProvider {

  override def apply(mode: Mode, index: Index): UserAnswersNavigator =
    new AuthorisationNavigator(mode, index)
}

trait AuthorisationNavigatorProvider {
  def apply(mode: Mode, index: Index): UserAnswersNavigator
}

class AuthorisationNavigator(override val mode: Mode, index: Index) extends UserAnswersNavigator {

  override type T = AuthorisationDomain

  implicit override val reader: UserAnswersReader[AuthorisationDomain] =
    AuthorisationDomain.userAnswersReader(index)
}
