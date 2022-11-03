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

import base.SpecBase
import play.api.libs.json._

class RichJsArraySpec extends SpecBase {

  "pickValuesByPath" - {

    "must extract values in JsArray at a given path as a list of options" in {

      val json = Json
        .parse("""
          |[
          |  {
          |     "field1": "value1_1",
          |     "field2": "value2_1"
          |  },
          |  {
          |     "field1": "value1_2"
          |  },
          |  {
          |     "field2": "value2_2"
          |  }
          |]

          |""".stripMargin)
        .as[JsArray]

      val result1 = json.pickValuesByPath[String](__ \ "field1")
      val result2 = json.pickValuesByPath[String](__ \ "field2")

      result1 mustBe Seq(Some("value1_1"), Some("value1_2"), None)
      result2 mustBe Seq(Some("value2_1"), None, Some("value2_2"))
    }
  }
}
