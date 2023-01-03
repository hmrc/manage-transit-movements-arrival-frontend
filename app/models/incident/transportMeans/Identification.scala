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

package models.incident.transportMeans

import models.{RadioModel, WithName}
import play.api.i18n.Messages

sealed trait Identification {
  val code: String

  def arg(implicit messages: Messages): String = messages(s"${Identification.messageKeyPrefix}.$this.arg")
}

object Identification extends RadioModel[Identification] {

  case object SeaGoingVessel extends WithName("seaGoingVessel") with Identification {
    override val code: String = "11"
  }

  case object IataFlightNumber extends WithName("iataFlightNumber") with Identification {
    override val code: String = "40"
  }

  case object InlandWaterwaysVehicle extends WithName("inlandWaterwaysVehicle") with Identification {
    override val code: String = "81"
  }

  case object ImoShipIdNumber extends WithName("imoShipIdNumber") with Identification {
    override val code: String = "10"
  }

  case object WagonNumber extends WithName("wagonNumber") with Identification {
    override val code: String = "20"
  }

  case object TrainNumber extends WithName("trainNumber") with Identification {
    override val code: String = "21"
  }

  case object RegNumberRoadVehicle extends WithName("regNumberRoadVehicle") with Identification {
    override val code: String = "30"
  }

  case object RegNumberRoadTrailer extends WithName("regNumberRoadTrailer") with Identification {
    override val code: String = "31"
  }

  case object RegNumberAircraft extends WithName("regNumberAircraft") with Identification {
    override val code: String = "41"
  }

  case object EuropeanVesselIdNumber extends WithName("europeanVesselIdNumber") with Identification {
    override val code: String = "80"
  }

  case object Unknown extends WithName("unknown") with Identification {
    override val code: String = "99"
  }

  override val messageKeyPrefix: String = "incident.transportMeans.identification"

  val values: Seq[Identification] = Seq(
    SeaGoingVessel,
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
