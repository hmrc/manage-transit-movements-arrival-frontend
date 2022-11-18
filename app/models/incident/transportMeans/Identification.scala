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

package models.incident.transportMeans

import models.{RadioModel, WithName}
import play.api.i18n.Messages

sealed trait Identification {
  def arg(implicit messages: Messages): String = messages(s"${Identification.messageKeyPrefix}.$this.arg")
}

object Identification extends RadioModel[Identification] {

  case object SeaGoingVehicle extends WithName("seaGoingVehicle") with Identification
  case object IataFlightNumber extends WithName("iataFlightNumber") with Identification
  case object InlandWaterwaysVehicle extends WithName("inlandWaterwaysVehicle") with Identification
  case object ImoShipIdNumber extends WithName("imoShipIdNumber") with Identification
  case object WagonNumber extends WithName("wagonNumber") with Identification
  case object TrainNumber extends WithName("trainNumber") with Identification
  case object RegNumberRoadVehicle extends WithName("regNumberRoadVehicle") with Identification
  case object RegNumberRoadTrailer extends WithName("regNumberRoadTrailer") with Identification
  case object RegNumberAircraft extends WithName("regNumberAircraft") with Identification
  case object EuropeanVesselIdNumber extends WithName("europeanVesselIdNumber") with Identification
  case object Unknown extends WithName("unknown") with Identification

  override val messageKeyPrefix: String = "incident.transportMeans.identification"

  val values: Seq[Identification] = Seq(
    SeaGoingVehicle,
    IataFlightNumber,
    InlandWaterwaysVehicle,
    ImoShipIdNumber,
    WagonNumber,
    TrainNumber,
    RegNumberRoadVehicle,
    RegNumberRoadTrailer,
    RegNumberAircraft,
    EuropeanVesselIdNumber,
    Unknown
  )
}
