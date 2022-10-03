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

package models.incident

import models.{RadioModel, WithName}

sealed trait IncidentCode

object IncidentCode extends RadioModel[IncidentCode] {

  case object Option1 extends WithName("1") with IncidentCode
  case object Option2 extends WithName("2") with IncidentCode
  case object Option3 extends WithName("3") with IncidentCode
  case object Option4 extends WithName("4") with IncidentCode
  case object Option5 extends WithName("5") with IncidentCode
  case object Option6 extends WithName("6") with IncidentCode

  override val messageKeyPrefix: String = "incident.incidentCode"

  val values: Seq[IncidentCode] = Seq(
    Option1,
    Option2,
    Option3,
    Option4,
    Option5,
    Option6
  )
}
