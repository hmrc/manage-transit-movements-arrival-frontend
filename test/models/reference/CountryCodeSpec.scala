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
import config.FrontendAppConfig
import org.scalacheck.Gen
import play.api.libs.json.{JsString, Json}
import play.api.test.Helpers.running

class CountryCodeSpec extends SpecBase {

  "CountryCode" - {

    "must serialise" in {
      forAll(Gen.alphaNumStr) {
        code =>
          val countryCode = CountryCode(code)
          Json.toJson(countryCode) mustEqual JsString(code)
      }
    }

    "must deserialise" - {
      "when reading from mongo" in {
        forAll(Gen.alphaNumStr) {
          code =>
            val countryCode = CountryCode(code)
            JsString(code).as[CountryCode] mustEqual countryCode
        }
      }

      "when reading from reference data" - {
        "when phase 5" in {
          running(_.configure("feature-flags.phase-6-enabled" -> false)) {
            app =>
              val config = app.injector.instanceOf[FrontendAppConfig]
              forAll(Gen.alphaNumStr) {
                code =>
                  Json
                    .parse(s"""
                         |{
                         |  "code": "$code"
                         |}
                         |""".stripMargin)
                    .as[CountryCode](CountryCode.reads(config)) mustEqual CountryCode(code)
              }
          }
        }

        "when phase 6" in {
          running(_.configure("feature-flags.phase-6-enabled" -> true)) {
            app =>
              val config = app.injector.instanceOf[FrontendAppConfig]
              forAll(Gen.alphaNumStr) {
                code =>
                  Json
                    .parse(s"""
                         |{
                         |  "key": "$code"
                         |}
                         |""".stripMargin)
                    .as[CountryCode](CountryCode.reads(config)) mustEqual CountryCode(code)
              }
          }
        }
      }
    }
  }
}
