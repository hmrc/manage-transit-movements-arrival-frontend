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
import models._
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.incident.IncidentsDomain

import javax.inject.{Inject, Singleton}

@Singleton
class IncidentsNavigatorProviderImpl @Inject() (implicit phaseConfig: PhaseConfig) extends IncidentsNavigatorProvider {

  override def apply(mode: Mode): UserAnswersNavigator =
    mode match {
      case NormalMode => new IncidentsNavigator(mode)
      case CheckMode  => new ArrivalNavigator(mode)
    }
}

trait IncidentsNavigatorProvider {
  def apply(mode: Mode): UserAnswersNavigator
}

class IncidentsNavigator(override val mode: Mode) extends UserAnswersNavigator {

  override type T = IncidentsDomain

  implicit override val reader: UserAnswersReader[IncidentsDomain] =
    IncidentsDomain.userAnswersReader
}
