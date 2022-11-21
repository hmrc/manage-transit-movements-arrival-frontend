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

import models.journeyDomain.UserAnswersReader
import models.journeyDomain.incident.equipment.seal.SealDomain
import models.{CheckMode, Index, Mode, NormalMode}

import javax.inject.{Inject, Singleton}

@Singleton
class SealNavigatorProviderImpl @Inject() () extends SealNavigatorProvider {

  override def apply(mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): UserAnswersNavigator =
    mode match {
      case NormalMode => new SealNavigator(mode, incidentIndex, equipmentIndex, sealIndex)
      case CheckMode  => new EquipmentNavigator(mode, incidentIndex, equipmentIndex)
    }
}

trait SealNavigatorProvider {
  def apply(mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index): UserAnswersNavigator
}

class SealNavigator(override val mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index) extends UserAnswersNavigator {

  override type T = SealDomain

  implicit override val reader: UserAnswersReader[SealDomain] =
    SealDomain.userAnswersReader(incidentIndex, equipmentIndex, sealIndex)
}
