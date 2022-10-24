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

import config.FrontendAppConfig
import models.Mode
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.identification.IdentificationDomain

import javax.inject.{Inject, Singleton}

@Singleton
class IdentificationNavigatorProviderImpl @Inject() (implicit config: FrontendAppConfig) extends IdentificationNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    new IdentificationNavigator(mode)
}

trait IdentificationNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class IdentificationNavigator(override val mode: Mode)(implicit override val config: FrontendAppConfig) extends UserAnswersNavigator {

  override type T = IdentificationDomain

  implicit override val reader: UserAnswersReader[IdentificationDomain] =
    IdentificationDomain.userAnswersReader
}
