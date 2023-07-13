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
import play.api.libs.json.{JsString, Json}

class CountryCodeSpec extends SpecBase {

  "must deserialise" - {
    "when country code is its own object" in {
      val json = JsString("foo")
      json.as[CountryCode] mustBe CountryCode("foo")
    }

    "when country code is nested inside an object" in {
      val json = Json.parse(s"""
           |{
           |  "code": "foo"
           |}
           |""".stripMargin)

      json.as[CountryCode] mustBe CountryCode("foo")
    }
  }

}
