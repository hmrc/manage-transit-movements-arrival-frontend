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
import cats.data.NonEmptyList
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalTo, get, okJson, urlEqualTo}
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import generators.Generators
import helper.WireMockServerHandler
import models.reference._
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with AppWithDefaultMockFixtures with WireMockServerHandler with ScalaCheckPropertyChecks with Generators {

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.customs-reference-data.port" -> server.port())

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val unlocodesResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "state": "valid",
      |      "activeFrom": "2021-02-15",
      |      "unLocodeExtendedCode": "code1",
      |      "name": "name1",
      |      "function": "--34-6--",
      |      "status": "AI",
      |      "date": "0601",
      |      "coordinates": "4230N 00131E",
      |      "comment": "Muy Vella"
      |    },
      |    {
      |      "state": "valid",
      |      "activeFrom": "2021-02-15",
      |      "unLocodeExtendedCode": "code2",
      |      "name": "name2",
      |      "function": "--3-----",
      |      "status": "RL",
      |      "date": "0307",
      |      "coordinates": "4234N 00135E"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val customsOfficesResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "id" : "GBtestId1",
      |      "name" : "testName1",
      |      "roles" : ["DES"],
      |      "phoneNumber" : "testPhoneNumber"
      |    },
      |    {
      |      "id" : "GBtestId2",
      |      "name" : "testName2",
      |      "roles" : ["DES"]
      |    }
      |  ]
      |}
      |""".stripMargin

  private val countriesResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "code": "GB",
      |      "state": "valid",
      |      "description": "United Kingdom"
      |    },
      |    {
      |      "code": "AD",
      |      "state": "valid",
      |      "description": "Andorra"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val typeOfLocationResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "type": "A",
      |      "description": "Designated location"
      |    },
      |    {
      |      "type": "B",
      |      "description": "Authorised place"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val nationalitiesResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "code": "GB",
      |      "description": "United Kingdom"
      |    },
      |    {
      |      "code": "AD",
      |      "description": "Andorra"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val incidentIdentifiersResponseJson: String =
    """
    |{
    |  "data": [
    |    {
    |     "qualifier": "U",
    |     "description": "UN/LOCODE"
    |    },
    |    {
    |     "qualifier": "W",
    |     "description": "GPS coordinates"
    |    }
    |  ]
    |}
    |""".stripMargin

  private val transportIdentifiersResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |     "type": "10",
      |     "description": "IMO Ship Identification Number"
      |    },
      |    {
      |     "type": "11",
      |     "description": "Name of the sea-going vessel"
      |    },
      |    {
      |     "type": "20",
      |     "description": "Wagon Number"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val identifiersResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |     "qualifier": "T",
      |     "description": "Postal code"
      |    },
      |    {
      |     "qualifier": "U",
      |     "description": "UN/LOCODE"
      |    },
      |    {
      |     "qualifier": "W",
      |     "description": "GPS coordinates"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val incidentCodeResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "code": "1",
      |      "description": "The carrier is obliged to deviate from..."
      |    },
      |    {
      |      "code": "2",
      |     "description": "Seals are broken or tampered with..."
      |    }
      |  ]
      |}
      |""".stripMargin

  private val countryCodesResponseJson: String =
    """
      |{
      |  "data": [
      |    {
      |      "code": "GB"
      |    },
      |    {
      |      "code": "AD"
      |    }
      |  ]
      |}
      |""".stripMargin

  private val emptyResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOfficesForCountry" - {
      val countryCode = CountryCode("GB")
      val url         = s"/$baseUrl/filtered-lists/CustomsOffices?data.countryId=${countryCode.code}&data.roles.role=DES"

      "must return a successful future response with a sequence of CustomsOffices" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(customsOfficesResponseJson))
        )

        val expectedResult = NonEmptyList(
          CustomsOffice("GBtestId1", Some("testName1"), Some("testPhoneNumber")),
          List(CustomsOffice("GBtestId2", Some("testName2"), None))
        )

        connector.getCustomsOfficesForCountry(countryCode).futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCustomsOfficesForCountry(countryCode))
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCustomsOfficesForCountry(countryCode))
      }
    }

    "getCountries" - {
      "for a given list name" - {
        "must return Seq of Country when successful" in {
          forAll(Gen.alphaNumStr) {
            listName =>
              server.stubFor(
                get(urlEqualTo(s"/$baseUrl/lists/$listName"))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseJson))
              )

              val expectedResult: NonEmptyList[Country] = NonEmptyList(
                Country(CountryCode("GB"), "United Kingdom"),
                List(Country(CountryCode("AD"), "Andorra"))
              )

              connector.getCountries(listName).futureValue mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          forAll(Gen.alphaNumStr) {
            listName =>
              checkNoReferenceDataFoundResponse(s"/$baseUrl/lists/$listName", connector.getCountries(listName))
          }
        }

        "must return an exception when an error response is returned" in {
          forAll(Gen.alphaNumStr) {
            listName =>
              checkErrorResponse(s"/$baseUrl/lists/$listName", connector.getCountries(listName))
          }
        }
      }
    }

    "getUnLocodes" - {
      val url = s"/$baseUrl/lists/UnLocodeExtended"

      "must return a successful future response with a sequence of UnLocodes" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(unlocodesResponseJson))
        )

        val expectedResult = NonEmptyList(
          UnLocode("code1", "name1"),
          List(UnLocode("code2", "name2"))
        )

        connector.getUnLocodes().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getUnLocodes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getUnLocodes())
      }
    }

    "getNationalities" - {
      val url = s"/$baseUrl/lists/Nationality"

      "must return a successful future response with a sequence of Nationalities" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(nationalitiesResponseJson))
        )

        val expectedResult = NonEmptyList(
          Nationality("GB", "United Kingdom"),
          List(Nationality("AD", "Andorra"))
        )

        connector.getNationalities().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getNationalities())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getNationalities())
      }
    }

    "getIncidentCodes" - {
      val url = s"/$baseUrl/lists/IncidentCode"

      "must return a successful future response with a sequence of IncidentCodes" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(incidentCodeResponseJson))
        )

        val expectedResult = NonEmptyList(
          IncidentCode("1", "The carrier is obliged to deviate from..."),
          List(IncidentCode("2", "Seals are broken or tampered with..."))
        )

        connector.getIncidentCodes().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getIncidentCodes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getIncidentCodes())
      }
    }

    "getTypesOfLocation" - {
      val url = s"/$baseUrl/lists/TypeOfLocation"

      "must return a successful future response with a sequence of LocationType" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(typeOfLocationResponseJson))
        )

        val expectedResult = NonEmptyList(
          TypeOfLocation("A", "Designated location"),
          List(TypeOfLocation("B", "Authorised place"))
        )

        connector.getTypesOfLocation().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getTypesOfLocation())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getTypesOfLocation())
      }
    }

    "getIncidentIdentifications" - {
      val url = s"/$baseUrl/lists/QualifierOfIdentificationIncident"

      "must return a successful future response with a sequence of IncidentIdentifications" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(incidentIdentifiersResponseJson))
        )

        val expectedResult = NonEmptyList(
          QualifierOfIdentification("U", "UN/LOCODE"),
          List(QualifierOfIdentification("W", "GPS coordinates"))
        )
        connector.getIncidentIdentifications().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getIncidentIdentifications())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getIncidentIdentifications())
      }
    }

    "getIdentifications" - {
      val url = s"/$baseUrl/lists/QualifierOfTheIdentification"

      "must return a successful future response with a sequence of Identifications" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(identifiersResponseJson))
        )

        val expectedResult = NonEmptyList(
          QualifierOfIdentification("T", "Postal code"),
          List(
            QualifierOfIdentification("U", "UN/LOCODE"),
            QualifierOfIdentification("W", "GPS coordinates")
          )
        )
        connector.getIdentifications().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getIdentifications())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getIdentifications())
      }
    }

    "getTransportIdentifications" - {
      val url = s"/$baseUrl/lists/TypeOfIdentificationOfMeansOfTransport"

      "must return a successful future response with a sequence of  Identifications" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(transportIdentifiersResponseJson))
        )

        val expectedResult: NonEmptyList[Identification] = NonEmptyList(
          Identification("10", "IMO Ship Identification Number"),
          List(
            Identification("11", "Name of the sea-going vessel"),
            Identification("20", "Wagon Number")
          )
        )
        connector.getTransportIdentifications().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getTransportIdentifications())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getTransportIdentifications())
      }
    }

    "getCountriesWithoutZip" - {
      val url = s"/$baseUrl/lists/CountryWithoutZip"

      "must return a successful future response with a sequence of country codes" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(countryCodesResponseJson))
        )

        val expectedResult = NonEmptyList(
          CountryCode("GB"),
          List(CountryCode("AD"))
        )

        connector.getCountriesWithoutZip().futureValue mustBe expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCountriesWithoutZip())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountriesWithoutZip())
      }
    }

    "getCountryWithoutZip" - {
      val countryCode = CountryCode("GB")
      val url         = s"/$baseUrl/filtered-lists/CountryWithoutZip?data.code=${countryCode.code}"

      "must return a successful future response with a country code" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(countryCodesResponseJson))
        )

        connector.getCountryWithoutZip(countryCode).futureValue mustBe countryCode
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, connector.getCountryWithoutZip(countryCode))
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountryWithoutZip(countryCode))
      }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, result: => Future[_]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(emptyResponseJson))
    )

    whenReady[Throwable, Assertion](result.failed) {
      _ mustBe a[NoReferenceDataFoundException]
    }
  }

  private def checkErrorResponse(url: String, result: => Future[_]): Assertion = {
    val errorResponses: Gen[Int] = Gen.chooseNum(400: Int, 599: Int)

    forAll(errorResponses) {
      errorResponse =>
        server.stubFor(
          get(urlEqualTo(url))
            .willReturn(
              aResponse()
                .withStatus(errorResponse)
            )
        )

        whenReady[Throwable, Assertion](result.failed) {
          _ mustBe an[Exception]
        }
    }
  }

}
