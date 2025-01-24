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

package config

object Constants {

  val GB = "GB"
  val XI = "XI"

  object IncidentCode {
    lazy val DeviatedFromItineraryCode         = "1"
    lazy val SealsBrokenOrTamperedCode         = "2"
    lazy val TransferredToAnotherTransportCode = "3"
    lazy val PartiallyOrFullyUnloadedCode      = "4"
    lazy val CarrierUnableToComplyCode         = "5"
    lazy val UnexpectedlyChangedCode           = "6"
  }

  object QualifierCode {
    lazy val UnlocodeCode            = "U"
    lazy val CustomsOfficeCode       = "V"
    lazy val CoordinatesCode         = "W"
    lazy val EoriNumberCode          = "X"
    lazy val AuthorisationNumberCode = "Y"
    lazy val AddressCode             = "Z"
  }

  object LocationType {
    lazy val DesignatedLocation = "A"
    lazy val AuthorisedPlace    = "B"
    lazy val ApprovedPlace      = "C"
    lazy val Other              = "D"
  }
}
