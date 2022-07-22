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
import models.journeyDomain.identification.{AuthorisationDomain, IdentificationDomain}

import javax.inject.{Inject, Singleton}

@Singleton
class AuthorisationNavigatorProviderImpl @Inject() () extends AuthorisationNavigatorProvider {

  def apply(index: Index): AuthorisationNavigator =
    new AuthorisationNavigator(index)
}

trait AuthorisationNavigatorProvider {

  def apply(index: Index): AuthorisationNavigator
}

class AuthorisationNavigator(
  index: Index
) extends UserAnswersNavigator[AuthorisationDomain, IdentificationDomain]()(
      AuthorisationDomain.userAnswersReader(index),
      IdentificationDomain.userAnswersReader
    )
