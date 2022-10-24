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

package models

import models.reference.Country
import play.api.libs.json.{Json, OFormat}

trait Address {
  val line1: String
  val line2: String
  val postalCode: String
}

case class UkAddress(
  line1: String,
  line2: String,
  postalCode: String
) extends Address {
  override def toString: String = Seq(line1, line2, postalCode).mkString("<br>")
}

object UkAddress {
  implicit val format: OFormat[UkAddress] = Json.format[UkAddress]
}

case class PostalCodeAddress(
  streetNumber: String,
  postalCode: String,
  country: Country
) {
  override def toString: String = Seq(streetNumber, postalCode, country.description).mkString("<br>")
}

object PostalCodeAddress {
  implicit val format: OFormat[PostalCodeAddress] = Json.format[PostalCodeAddress]
}

case class InternationalAddress(
  line1: String,
  line2: String,
  postalCode: String,
  country: Country
) extends Address {
  override def toString: String = Seq(line1, line2, postalCode, country.description).mkString("<br>")
}

object InternationalAddress {
  implicit val format: OFormat[InternationalAddress] = Json.format[InternationalAddress]
}
