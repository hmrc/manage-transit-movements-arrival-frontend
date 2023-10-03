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

package models.reference

import base.SpecBase
import models.reference.TypeOfLocation._
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json

class TypeOfLocationSpec extends SpecBase with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "TypeOfLocation" - {

    "must deserialise valid values" in {

      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val typeOfLocation = TypeOfLocation(code, description)
          Json
            .parse(s"""
                 |{
                 |  "type": "$code",
                 |  "description": "$description"
                 |}
                 |""".stripMargin)
            .as[TypeOfLocation] mustBe typeOfLocation
      }
    }

    "must serialise" in {

      forAll(Gen.alphaNumStr, Gen.alphaNumStr) {
        (code, description) =>
          val typeOfLocation = TypeOfLocation(code, description)
          Json.toJson(typeOfLocation) mustBe Json.parse(s"""
               |{
               |  "type": "$code",
               |  "description": "$description"
               |}
               |""".stripMargin)
      }
    }
  }
}
