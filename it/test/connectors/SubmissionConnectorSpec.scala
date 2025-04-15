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

package connectors

import com.github.tomakehurst.wiremock.client.WireMock.*
import itbase.{ItSpecBase, WireMockServerHandler}
import models.{ArrivalMessage, ArrivalMessages}
import org.scalacheck.Gen
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.*
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDateTime

class SubmissionConnectorSpec extends ItSpecBase with WireMockServerHandler {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.manage-transit-movements-arrival-cache.port" -> server.port())

  private lazy val connector: SubmissionConnector = app.injector.instanceOf[SubmissionConnector]

  "SubmissionConnector" - {

    "post" - {

      val url = s"/manage-transit-movements-arrival-cache/declaration/submit"

      "must return true when status is Ok" in {
        server.stubFor(
          post(urlEqualTo(url))
            .withHeader("API-Version", equalTo("1.0"))
            .withRequestBody(equalToJson(mrn.toString))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: HttpResponse = await(connector.post(mrn))

        result.status mustEqual OK
      }

      "return false for 4xx response" in {
        val status = Gen.choose(400: Int, 499: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .withHeader("API-Version", equalTo("1.0"))
            .withRequestBody(equalToJson(mrn.toString))
            .willReturn(aResponse().withStatus(status))
        )

        val result: HttpResponse = await(connector.post(mrn))

        result.status mustEqual status
      }

      "return false for 5xx response" in {
        val status = Gen.choose(500: Int, 599: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .withHeader("API-Version", equalTo("1.0"))
            .withRequestBody(equalToJson(mrn.toString))
            .willReturn(aResponse().withStatus(status))
        )

        val result: HttpResponse = await(connector.post(mrn))

        result.status mustEqual status
      }
    }

    "getMessages" - {

      val url = s"/manage-transit-movements-arrival-cache/messages/$mrn"

      val json =
        """
          |{
          |  "messages" : [
          |    {
          |      "type" : "IE007",
          |      "received" : "2022-11-10T15:32:51.000Z"
          |    },
          |    {
          |      "type" : "IE043",
          |      "received" : "2022-11-10T16:32:51.000Z"
          |    }
          |  ]
          |}
          |""".stripMargin

      "must return messages when status is Ok" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(json))
        )

        val result: ArrivalMessages = await(connector.getMessages(mrn))

        result mustEqual ArrivalMessages(
          Seq(
            ArrivalMessage("IE007", LocalDateTime.of(2022, 11, 10, 15, 32, 51)),
            ArrivalMessage("IE043", LocalDateTime.of(2022, 11, 10, 16, 32, 51))
          )
        )
      }
    }
  }
}
