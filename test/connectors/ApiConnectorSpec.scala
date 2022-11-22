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

package connectors

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock._
import generators.{ArrivalUserAnswersGenerator, Generators}
import helper.WireMockServerHandler
import models.UserAnswers
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse

class ApiConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with Generators with ArrivalUserAnswersGenerator {

  val uA: UserAnswers = arbitraryArrivalAnswers(emptyUserAnswers).sample.value

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.common-transit-convention-traders.port" -> server.port())

  private lazy val connector: ApiConnector = app.injector.instanceOf[ApiConnector]

  val arrivalId: String = "someid"

  val expected: String = Json
    .obj(
      "_links" -> Json.obj(
        "self" -> Json.obj(
          "href" -> s"/customs/transits/movements/arrivals/$arrivalId"
        ),
        "messages" -> Json.obj(
          "href" -> s"/customs/transits/movements/arrivals/$arrivalId/messages"
        )
      )
    )
    .toString()
    .stripMargin

  val uri = "/movements/arrivals"

  "ApiConnector" - {

    "submitDeclaration is called" - {

      "for success" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(okJson(expected)))

        val res: HttpResponse = await(connector.submitDeclaration(uA))
        res.status mustBe OK

      }

      "for bad request" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(badRequest()))

        val res: HttpResponse = await(connector.submitDeclaration(uA))
        res.status mustBe BAD_REQUEST

      }

      "for internal server error" in {

        server.stubFor(post(urlEqualTo(uri)).willReturn(serverError()))

        val res: HttpResponse = await(connector.submitDeclaration(uA))
        res.status mustBe INTERNAL_SERVER_ERROR

      }

    }

  }

}