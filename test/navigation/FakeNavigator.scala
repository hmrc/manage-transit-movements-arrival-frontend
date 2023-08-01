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

import config.PhaseConfig
import models.{Index, Mode, UserAnswers}
import play.api.mvc.Call

class FakeNavigator(desiredRoute: Call) extends Navigator {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeArrivalNavigator(desiredRoute: Call, mode: Mode)(implicit phaseConfig: PhaseConfig) extends ArrivalNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeIncidentsNavigator(desiredRoute: Call, mode: Mode)(implicit phaseConfig: PhaseConfig) extends IncidentsNavigator(mode) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeIncidentNavigator(desiredRoute: Call, index: Index, mode: Mode)(implicit phaseConfig: PhaseConfig) extends IncidentNavigator(mode, index) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeEquipmentsNavigator(desiredRoute: Call, incidentIndex: Index, mode: Mode)(implicit phaseConfig: PhaseConfig)
    extends EquipmentsNavigator(mode, incidentIndex) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeEquipmentNavigator(desiredRoute: Call, incidentIndex: Index, equipmentIndex: Index, mode: Mode)(implicit phaseConfig: PhaseConfig)
    extends EquipmentNavigator(mode, incidentIndex, equipmentIndex) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeSealNavigator(desiredRoute: Call, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index, mode: Mode)(implicit phaseConfig: PhaseConfig)
    extends SealNavigator(mode, incidentIndex, equipmentIndex, sealIndex) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}

class FakeItemNumberNavigator(desiredRoute: Call, incidentIndex: Index, equipmentIndex: Index, itemNumberIndex: Index, mode: Mode)(implicit
  phaseConfig: PhaseConfig
) extends ItemNumberNavigator(mode, incidentIndex, equipmentIndex, itemNumberIndex) {
  override def nextPage(userAnswers: UserAnswers): Call = desiredRoute
}
