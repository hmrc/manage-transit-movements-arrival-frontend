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
import models.reference.Identification
import models.reference.Identification._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class IdentificationSpec extends SpecBase with Matchers with ScalaCheckPropertyChecks with OptionValues {
  private val id1 = Identification("U", "UN/LOCODE")
  private val id2 = Identification("W", "GPS coordinates")
  private val id3 = Identification("Z", "Free text")
  private val ids = Seq(id1, id2, id3)

  "Identification" - {

    "must deserialise valid values" in {

      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val identification = Identification(code, description)
          Json
            .parse(s"""
                 |{
                 |  "qualifier": "$code",
                 |  "description": "$description"
                 |}
                 |""".stripMargin)
            .as[Identification] mustBe identification
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!ids.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[Identification] mustEqual JsError("error.expected.jsobject")
      }
    }

    "must serialise" in {

      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val identification = Identification(code, description)
          Json.toJson(identification) mustBe Json.parse(s"""
               |{
               |  "qualifier": "$code",
               |  "description": "$description"
               |}
               |""".stripMargin)
      }
    }
  }
}
