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

import com.github.tomakehurst.wiremock.client.WireMock._
import itbase.{ItSpecBase, WireMockServerHandler}
import models.UserAnswers
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._

class CacheConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.manage-transit-movements-arrival-cache.port" -> server.port())

  private lazy val connector: CacheConnector = app.injector.instanceOf[CacheConnector]

  private lazy val json: String =
    s"""
      |{
      |    "_id" : "2e8ede47-dbfb-44ea-a1e3-6c57b1fe6fe2",
      |    "mrn" : "$mrn",
      |    "eoriNumber" : "GB1234567",
      |    "data" : {},
      |    "tasks" : {},
      |    "createdAt" : "2022-09-05T15:58:44.188Z",
      |    "lastUpdated" : "2022-09-07T10:33:23.472Z",
      |    "submissionStatus" : "notSubmitted"
      |}
      |""".stripMargin

  private lazy val userAnswers = Json.parse(json).as[UserAnswers]

  "CacheConnector" - {

    "get" - {

      lazy val url = s"/manage-transit-movements-arrival-cache/user-answers/${mrn.toString}"

      "must return user answers when status is Ok" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(okJson(json))
        )

        connector.get(mrn.toString).futureValue mustBe Some(userAnswers)
      }

      "return None when no cached data found for provided mrn" in {
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(notFound())
        )

        val result: Option[UserAnswers] = await(connector.get(mrn.toString))

        result mustBe None
      }
    }

    "post" - {

      lazy val url = s"/manage-transit-movements-arrival-cache/user-answers/${mrn.toString}"

      "must return true when status is Ok" in {
        server.stubFor(
          post(urlEqualTo(url))
            .withRequestBody(equalToJson(userAnswers))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = await(connector.post(userAnswers))

        result mustBe true
      }

      "return false for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          post(urlEqualTo(url))
            .withRequestBody(equalToJson(userAnswers))
            .willReturn(aResponse().withStatus(status))
        )

        val result: Boolean = await(connector.post(userAnswers))

        result mustBe false
      }
    }

    "put" - {

      val url = s"/manage-transit-movements-arrival-cache/user-answers"

      "must return true when status is Ok" in {
        server.stubFor(
          put(urlEqualTo(url))
            .withRequestBody(equalToJson(mrn.toString))
            .willReturn(aResponse().withStatus(OK))
        )

        val result: Boolean = await(connector.put(mrn.toString))

        result mustBe true
      }

      "return false for 4xx or 5xx response" in {
        val status = Gen.choose(400: Int, 599: Int).sample.value

        server.stubFor(
          put(urlEqualTo(url))
            .willReturn(aResponse().withStatus(status))
        )

        val result: Boolean = await(connector.put(mrn.toString))

        result mustBe false
      }
    }

    "checkLock" - {

      lazy val url = s"/manage-transit-movements-arrival-cache/user-answers/${mrn.toString}/lock"

      "must return true when status is Ok" in {
        server.stubFor(get(urlEqualTo(url)) `willReturn` aResponse().withStatus(OK))

        val result: Boolean = await(connector.checkLock(userAnswers))

        result mustBe true
      }

      "return false for other responses" in {

        server.stubFor(get(urlEqualTo(url)) `willReturn` aResponse().withStatus(BAD_REQUEST))

        val result: Boolean = await(connector.checkLock(userAnswers))

        result mustBe false
      }
    }

    "deleteLock" - {

      lazy val url = s"/manage-transit-movements-arrival-cache/user-answers/${mrn.toString}/lock"

      "must return true when status is Ok" in {
        server.stubFor(delete(urlEqualTo(url)) `willReturn` aResponse().withStatus(OK))

        val result: Boolean = await(connector.deleteLock(userAnswers))

        result mustBe true
      }

      "return false for other responses" in {

        val errorResponses: Gen[Int] = Gen
          .chooseNum(400: Int, 599: Int)

        forAll(errorResponses) {
          error =>
            server.stubFor(
              delete(urlEqualTo(url))
                .willReturn(aResponse().withStatus(error))
            )

            val result: Boolean = await(connector.deleteLock(userAnswers))

            result mustBe false
        }
      }
    }
  }

}
