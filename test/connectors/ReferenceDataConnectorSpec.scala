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

import base.{AppWithDefaultMockFixtures, SpecBase}
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, get, okJson, urlEqualTo}
import generators.Generators
import helper.WireMockServerHandler
import models.NationalityList
import models.reference._
import org.scalacheck.Gen
import org.scalatest.Assertion
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with Generators {

  private val startUrl = "test-only/transit-movements-trader-reference-data"
  private val country  = CountryCode("GB")

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.referenceData.port" -> server.port())

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val unlocodeResponseJson: String =
    """
      |
      |[
      |  {
      |    "state": "valid",
      |    "activeFrom": "2021-02-15",
      |    "unLocodeExtendedCode": "code1",
      |    "name": "name1",
      |    "function": "--34-6--",
      |    "status": "AI",
      |    "date": "0601",
      |    "coordinates": "4230N 00131E",
      |    "comment": "Muy Vella"
      |  },
      |  {
      |    "state": "valid",
      |    "activeFrom": "2021-02-15",
      |    "unLocodeExtendedCode": "code2",
      |    "name": "name2",
      |    "function": "--3-----",
      |    "status": "RL",
      |    "date": "0307",
      |    "coordinates": "4234N 00135E"
      |  }
      |]
      |
      |""".stripMargin

  private val customsOfficeResponseJson: String =
    """
      |[
      | {
      |   "id" : "GBtestId1",
      |   "name" : "testName1",
      |   "roles" : ["role1", "role2"],
      |   "phoneNumber" : "testPhoneNumber"
      | },
      | {
      |   "id" : "GBtestId2",
      |   "name" : "testName2",
      |   "roles" : ["role1", "role2"]
      | }
      |]
      |""".stripMargin

  private val countryListResponseJson: String =
    """
      |[
      | {
      |   "code":"GB",
      |   "state":"valid",
      |   "description":"United Kingdom"
      | },
      | {
      |   "code":"AD",
      |   "state":"valid",
      |   "description":"Andorra"
      | }
      |]
      |""".stripMargin

  private val transportDataJson: String =
    """
      |{
      |  "nationalities": [
      |    {
      |      "code":"GB",
      |      "desc":"United Kingdom"
      |    },
      |    {
      |      "code":"AD",
      |      "desc":"Andorra"
      |    }
      |  ]
      |}
      |""".stripMargin

  val errorResponses: Gen[Int] = Gen.chooseNum(400, 599)

  "Reference Data" - {

    "getCustomsOffices" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult =
          Seq(
            CustomsOffice("GBtestId1", Some("testName1"), Some("testPhoneNumber")),
            CustomsOffice("GBtestId2", Some("testName2"), None)
          )

        connector.getCustomsOffices().futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/customs-offices", connector.getCustomsOffices())
      }
    }

    "getCustomsOfficesOfDepartureForCountry" - {
      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-offices/GB?role=DES"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = Seq(
          CustomsOffice("GBtestId1", Some("testName1"), Some("testPhoneNumber")),
          CustomsOffice("GBtestId2", Some("testName2"), None)
        )

        connector.getCustomsOfficesForCountry(country).futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/customs-offices-p5/$country?role=DES", connector.getCustomsOfficesForCountry(country))
      }
    }

    "getCountries" - {

      "for all countries must" - {

        "return Seq of Country when successful" in {
          server.stubFor(
            get(urlEqualTo(s"/$startUrl/countries"))
              .willReturn(okJson(countryListResponseJson))
          )

          val expectedResult: Seq[Country] = Seq(
            Country(CountryCode("GB"), "United Kingdom"),
            Country(CountryCode("AD"), "Andorra")
          )

          connector.getCountries(Nil).futureValue mustEqual expectedResult
        }

        "return an exception when an error response is returned" in {
          checkErrorResponse(s"/$startUrl/countries", connector.getCountries(Nil))
        }
      }

      "for transit countries must" - {

        val queryParameters = Seq("membership" -> "ctc")

        "return Seq of Country when successful" in {
          server.stubFor(
            get(urlEqualTo(s"/$startUrl/countries?membership=ctc"))
              .willReturn(okJson(countryListResponseJson))
          )

          val expectedResult: Seq[Country] = Seq(
            Country(CountryCode("GB"), "United Kingdom"),
            Country(CountryCode("AD"), "Andorra")
          )

          connector.getCountries(queryParameters).futureValue mustEqual expectedResult
        }

        "return an exception when an error response is returned" in {
          checkErrorResponse(s"/$startUrl/countries?membership=ctc", connector.getCountries(queryParameters))
        }
      }

    }

    "getUnLocodes" - {

      "must return a successful future response with a sequence of UnLocodes" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/un-locodes"))
            .willReturn(okJson(unlocodeResponseJson))
        )

        val expectedResult =
          Seq(
            UnLocode("code1", "name1"),
            UnLocode("code2", "name2")
          )

        connector.getUnLocodes().futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/un-locodes", connector.getUnLocodes())
      }

    }

    "getAddressPostcodeBasedCountries" - {
      "must return Seq of Country when successful" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/country-address-postcode-based"))
            .willReturn(okJson(countryListResponseJson))
        )

        val expectedResult: Seq[Country] = Seq(
          Country(CountryCode("GB"), "United Kingdom"),
          Country(CountryCode("AD"), "Andorra")
        )

        connector.getAddressPostcodeBasedCountries().futureValue mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/country-address-postcode-based", connector.getAddressPostcodeBasedCountries())
      }
    }

    "getNationalities" - {

      "must return a successful future response with a sequence of Nationalities" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/transport"))
            .willReturn(okJson(transportDataJson))
        )

        val expectedResult = NationalityList(
          Seq(
            Nationality("GB", "United Kingdom"),
            Nationality("AD", "Andorra")
          )
        )

        connector.getTransportData().futureValue mustBe expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(s"/$startUrl/nationalities", connector.getTransportData())
      }

    }

  }

  private def checkErrorResponse(url: String, result: Future[_]): Assertion =
    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        whenReady(result.failed) {
          _ mustBe an[Exception]
        }
    }

}
