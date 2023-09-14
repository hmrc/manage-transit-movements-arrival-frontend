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

import base.SpecBase
import models.incident.transportMeans.Identification._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
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

    "must convert to string" - {

      "when sea going vessel" in {
        val result = SeaGoingVessel.toString
        result mustBe "seaGoingVessel"
      }
      "when IATA flight number" in {
        val result = IataFlightNumber.toString
        result mustBe "iataFlightNumber"
      }
      "when inland waterways vehicle" in {
        val result = InlandWaterwaysVehicle.toString
        result mustBe "inlandWaterwaysVehicle"
      }
      "when IMO ship identification number" in {
        val result = ImoShipIdNumber.toString
        result mustBe "imoShipIdNumber"
      }
      "when wagon number" in {
        val result = WagonNumber.toString
        result mustBe "wagonNumber"
      }
      "when train number" in {
        val result = TrainNumber.toString
        result mustBe "trainNumber"
      }
      "when reg number of road vehicle" in {
        val result = RegNumberRoadVehicle.toString
        result mustBe "regNumberRoadVehicle"
      }
      "when reg number of road trailer" in {
        val result = RegNumberRoadTrailer.toString
        result mustBe "regNumberRoadTrailer"
      }
      "when reg number of aircraft" in {
        val result = RegNumberAircraft.toString
        result mustBe "regNumberAircraft"
      }
      "when european vessel identification number" in {
        val result = EuropeanVesselIdNumber.toString
        result mustBe "europeanVesselIdNumber"
      }
      "when unknown" in {
        val result = Unknown.toString
        result mustBe "unknown"
      }
    }

  }
}
