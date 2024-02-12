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
import models.journeyDomain.Read
import models.journeyDomain.incident.equipment.EquipmentsDomain

import javax.inject.{Inject, Singleton}

@Singleton
class EquipmentsNavigatorProviderImpl @Inject() extends EquipmentsNavigatorProvider {

  override def apply(mode: Mode, incidentIndex: Index): UserAnswersNavigator =
    mode match {
      case NormalMode => new EquipmentsNavigator(mode, incidentIndex)
      case CheckMode  => new IncidentNavigator(mode, incidentIndex)
    }
}

trait EquipmentsNavigatorProvider {
  def apply(mode: Mode, incidentIndex: Index): UserAnswersNavigator
}

class EquipmentsNavigator(override val mode: Mode, incidentIndex: Index) extends UserAnswersNavigator {

  override type T = EquipmentsDomain

  implicit override val reader: Read[EquipmentsDomain] =
    EquipmentsDomain.userAnswersReader(incidentIndex)
}
