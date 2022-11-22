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

import base.SpecBase
import generators.Generators
import models.incident.transportMeans.Identification._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import org.scalatest.matchers.must.Matchers
import org.scalatest.OptionValues
import play.api.libs.json.{JsError, JsString, Json}

class IdentificationSpec extends SpecBase with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "Identification" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(Identification.values)

      forAll(gen) {
        identification =>
          JsString(identification.toString).validate[Identification].asOpt.value mustEqual identification
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!Identification.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[Identification] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(Identification.values)

      forAll(gen) {
        identification =>
          Json.toJson(identification) mustEqual JsString(identification.toString)
      }
    }

    "must have an associated arg value" - {

      "when sea going vessel" in {
        val result = SeaGoingVehicle.arg
        result mustBe "name of the sea-going vessel"
      }
      "when IATA flight number" in {
        val result = IataFlightNumber.arg
        result mustBe "IATA flight number"
      }
      "when inland waterways vehicle" in {
        val result = InlandWaterwaysVehicle.arg
        result mustBe "name of the inland waterways vehicle"
      }
      "when IMO ship identification number" in {
        val result = ImoShipIdNumber.arg
        result mustBe "IMO ship identification number"
      }
      "when wagon number" in {
        val result = WagonNumber.arg
        result mustBe "wagon number"
      }
      "when train number" in {
        val result = TrainNumber.arg
        result mustBe "train number"
      }
      "when reg number of road vehicle" in {
        val result = RegNumberRoadVehicle.arg
        result mustBe "registration number of the road vehicle"
      }
      "when reg number of road trailer" in {
        val result = RegNumberRoadTrailer.arg
        result mustBe "registration number of the road trailer"
      }
      "when reg number of aircraft" in {
        val result = RegNumberAircraft.arg
        result mustBe "registration number of the aircraft"
      }
      "when european vessel identification number" in {
        val result = EuropeanVesselIdNumber.arg
        result mustBe "European vessel identification number (ENI code)"
      }
      "when unknown" in {
        val result = Unknown.arg
        result mustBe "identification number"
      }
    }

  }
}
