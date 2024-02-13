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

import models.journeyDomain.Read
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.{CheckMode, Index, Mode, NormalMode}

import javax.inject.{Inject, Singleton}

@Singleton
class EquipmentNavigatorProviderImpl @Inject() extends EquipmentNavigatorProvider {

  override def apply(mode: Mode, incidentIndex: Index, equipmentIndex: Index): UserAnswersNavigator =
    mode match {
      case NormalMode => new EquipmentNavigator(mode, incidentIndex, equipmentIndex)
      case CheckMode  => new EquipmentsNavigator(mode, incidentIndex)
    }
}

trait EquipmentNavigatorProvider {
  def apply(mode: Mode, incidentIndex: Index, equipmentIndex: Index): UserAnswersNavigator
}

class EquipmentNavigator(override val mode: Mode, incidentIndex: Index, equipmentIndex: Index) extends UserAnswersNavigator {

  override type T = EquipmentDomain

  implicit override val reader: Read[EquipmentDomain] =
    EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex)
}
