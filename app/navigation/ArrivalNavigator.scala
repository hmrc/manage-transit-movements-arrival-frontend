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

import models.Mode
import models.journeyDomain.{ArrivalDomain, Read}

import javax.inject.{Inject, Singleton}

@Singleton
class ArrivalNavigatorProviderImpl @Inject() extends ArrivalNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    new ArrivalNavigator(mode)
}

trait ArrivalNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class ArrivalNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = ArrivalDomain

  implicit override val reader: Read[ArrivalDomain] =
    ArrivalDomain.userAnswersReader
}
