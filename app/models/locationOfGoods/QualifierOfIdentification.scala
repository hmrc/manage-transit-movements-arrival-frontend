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

sealed trait QualifierOfIdentification

object QualifierOfIdentification extends RadioModel[QualifierOfIdentification] {

  case object CustomsOffice extends WithName("customsOffice") with QualifierOfIdentification
  case object EoriNumber extends WithName("eoriNumber") with QualifierOfIdentification
  case object AuthorisationNumber extends WithName("authorisationNumber") with QualifierOfIdentification
  case object Coordinates extends WithName("coordinates") with QualifierOfIdentification
  case object Unlocode extends WithName("unlocode") with QualifierOfIdentification
  case object Address extends WithName("address") with QualifierOfIdentification
  case object PostalCode extends WithName("postalCode") with QualifierOfIdentification

  override val messageKeyPrefix: String = "locationOfGoods.qualifierOfIdentification"

  val values: Seq[QualifierOfIdentification] = Seq(
    CustomsOffice,
    EoriNumber,
    AuthorisationNumber,
    Coordinates,
    Unlocode,
    Address,
    PostalCode
  )

  val locationValues: Seq[QualifierOfIdentification] = Seq(
    Coordinates,
    Unlocode,
    Address
  )
}
