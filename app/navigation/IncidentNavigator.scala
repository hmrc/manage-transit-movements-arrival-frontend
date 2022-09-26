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

import models.Index
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.incident.IncidentDomain

import javax.inject.{Inject, Singleton}

@Singleton
class IncidentNavigatorProviderImpl @Inject() () extends IncidentNavigatorProvider {

  def apply(index: Index): IncidentNavigator =
    new IncidentNavigator(index)
}

trait IncidentNavigatorProvider {

  def apply(index: Index): IncidentNavigator
}

class IncidentNavigator(
  index: Index
) extends UserAnswersNavigator[IncidentDomain, IdentificationDomain]()(
      IncidentDomain.userAnswersReader(index),
      IdentificationDomain.userAnswersReader
    )
