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

package models.incident

import base.SpecBase
import models.incident.IncidentCode._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class IncidentCodeSpec extends SpecBase with Matchers with ScalaCheckPropertyChecks with OptionValues {
  private val ic1 = IncidentCode("1", "test1")
  private val ic2 = IncidentCode("2", "test2")
  private val ics = Seq(ic1, ic2)

  "IncidentCode" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(ics)

      forAll(gen) {
        incidentCode =>
          JsString(incidentCode.toString).validate[IncidentCode].asOpt.value mustEqual incidentCode
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!ics.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[IncidentCode] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(ics)

      forAll(gen) {
        incidentCode =>
          Json.toJson(incidentCode) mustEqual JsString(incidentCode.toString)
      }
    }
  }
}
