/*
 * Copyright 2025 HM Revenue & Customs
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
import play.api.libs.json.Json

import java.time.LocalDateTime

class ArrivalMessageSpec extends SpecBase {

  "ArrivalMessage" - {

    "must be deserialisable" in {
      val json = Json.parse("""
          |{
          |  "_links": {
          |    "self": {
          |      "href": "/customs/transits/movements/arrivals/63498209a2d89ad8/messages/634982098f02f00a"
          |    },
          |    "arrival": {
          |      "href": "/customs/transits/movements/arrivals/63498209a2d89ad8"
          |    }
          |  },
          |  "id": "634982098f02f00a",
          |  "arrivalId": "63498209a2d89ad8",
          |  "received": "2022-11-10T15:32:51.459Z",
          |  "type": "IE007",
          |  "status": "Success"
          |}
          |""".stripMargin)

      val result = json.validate[ArrivalMessage]

      val expectedResult = ArrivalMessage(
        `type` = "IE007",
        received = LocalDateTime.of(2022, 11, 10, 15, 32, 51, 459000000)
      )

      result.get mustEqual expectedResult
    }
  }
}
