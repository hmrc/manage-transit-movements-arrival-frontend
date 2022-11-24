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

  case object DeviatedFromItinerary extends WithName("deviatedFromItinerary") with IncidentCode
  case object SealsBrokenOrTampered extends WithName("sealsBrokenOrTampered") with IncidentCode
  case object TransferredToAnotherTransport extends WithName("transferredToAnotherTransport") with IncidentCode
  case object PartiallyOrFullyUnloaded extends WithName("partiallyOrFullyUnloaded") with IncidentCode
  case object CarrierUnableToComply extends WithName("carrierUnableToComply") with IncidentCode
  case object UnexpectedlyChanged extends WithName("unexpectedlyChanged") with IncidentCode

  override val messageKeyPrefix: String = "incident.incidentCode"
  val prefixForDisplay: String          = "incident.incidentCode.forDisplay"

  val values: Seq[IncidentCode] = Seq(
    DeviatedFromItinerary,
    SealsBrokenOrTampered,
    TransferredToAnotherTransport,
    PartiallyOrFullyUnloaded,
    CarrierUnableToComply,
    UnexpectedlyChanged
  )
}
