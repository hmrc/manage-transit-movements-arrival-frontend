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

package models.locationOfGoods

import models.{RadioModel, WithName}

sealed trait TypeOfLocation

object TypeOfLocation extends RadioModel[TypeOfLocation] {

  case object AuthorisedPlace extends WithName("authorisedPlace") with TypeOfLocation
  case object DesignatedLocation extends WithName("designatedLocation") with TypeOfLocation
  case object ApprovedPlace extends WithName("approvedPlace") with TypeOfLocation
  case object Other extends WithName("other") with TypeOfLocation

  override val messageKeyPrefix: String = "locationOfGoods.typeOfLocation"

  val values: Seq[TypeOfLocation] = Seq(
    AuthorisedPlace,
    DesignatedLocation,
    ApprovedPlace,
    Other
  )
}
