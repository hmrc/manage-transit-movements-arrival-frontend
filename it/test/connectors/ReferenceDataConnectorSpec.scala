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

import cats.data.NonEmptySet
import com.github.tomakehurst.wiremock.client.WireMock.*
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import itbase.{ItSpecBase, WireMockServerHandler}
import models.reference.*
import org.scalacheck.Gen
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataConnectorSpec extends ItSpecBase with WireMockServerHandler with ScalaCheckPropertyChecks with EitherValues {

  private val baseUrl = "customs-reference-data/test-only"

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .configure(conf = "microservice.services.customs-reference-data.port" -> server.port())

  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val emptyResponseJson: String =
    """
      |[]
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOfficesForCountry" - {
      val countryIds = Seq("GB", "XI")

      val customsOfficesResponseJson: String =
        """
            |[
            |  {
            |    "customsOfficeLsd": {
            |      "languageCode": "EN",
            |      "customsOfficeUsualName": "Glasgow Airport"
            |    },
            |    "phoneNumber": "+44(0)300 106 3520",
            |    "referenceNumber": "GB000054",
            |    "countryCode": "GB"
            |  },
            |  {
            |    "customsOfficeLsd": {
            |      "languageCode": "EN",
            |      "customsOfficeUsualName": "Belfast International Airport"
            |    },
            |    "phoneNumber": "+44 (0)3000 575 988",
            |    "referenceNumber": "XI000014",
            |    "countryCode": "XI"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a sequence of CustomsOffices" in {
        val url        = s"/$baseUrl/lists/CustomsOffices?countryCodes=GB&countryCodes=XI&roles=DES"
        val countryIds = Seq("GB", "XI")

        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(customsOfficesResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          CustomsOffice("GB000054", "Glasgow Airport", Some("+44(0)300 106 3520"), "GB"),
          CustomsOffice("XI000014", "Belfast International Airport", Some("+44 (0)3000 575 988"), "XI")
        )

        connector.getCustomsOfficesForCountry(countryIds*).futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        val countryId = "AR"
        val url       = s"/$baseUrl/lists/CustomsOffices?countryCodes=AR&roles=DES"
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getCustomsOfficesForCountry(countryId))
      }

      "must return an exception when an error response is returned" in {
        val countryId = "GB"
        val url       = s"/$baseUrl/lists/CustomsOffices?countryCodes=GB&roles=DES"
        checkErrorResponse(url, connector.getCustomsOfficesForCountry(countryId))
      }
    }

    "getCountries" - {

      val countriesResponseJson: String =
        """
            |[
            |  {
            |    "key": "GB",
            |    "value": "United Kingdom"
            |  },
            |  {
            |    "key": "AD",
            |    "value": "Andorra"
            |  }
            |]
            |""".stripMargin

      "for a given list name" - {
        "must return Seq of Country when successful" in {
          forAll(Gen.alphaNumStr) {
            listName =>
              server.stubFor(
                get(urlEqualTo(s"/$baseUrl/lists/$listName"))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countriesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                Country(CountryCode("GB"), "United Kingdom"),
                Country(CountryCode("AD"), "Andorra")
              )

              connector.getCountries(listName).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          forAll(Gen.alphaNumStr) {
            listName =>
              checkNoReferenceDataFoundResponse(s"/$baseUrl/lists/$listName", emptyResponseJson, connector.getCountries(listName))
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

      val unlocodesResponseJson: String =
        """
            |[
            |  {
            |    "key": "code1",
            |    "value": "name1"
            |  },
            |  {
            |    "key": "code2",
            |    "value": "name2"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a sequence of UnLocodes" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(unlocodesResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          UnLocode("code1", "name1"),
          UnLocode("code2", "name2")
        )

        connector.getUnLocodes().futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getUnLocodes())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getUnLocodes())
      }
    }

    "getUnLocode" - {
      val code = "UN1"

      val url = s"/$baseUrl/lists/UnLocodeExtended?keys=UN1"

      val unLocodeResponseJson: String =
        """
            |[
            |  {
            |    "key": "UN1",
            |    "value": "testName1"
            |  }
            |]
            |""".stripMargin

      "must return a Seq of UN/LOCODES when successful" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(unLocodeResponseJson))
        )

        val expectedResult = UnLocode("UN1", "testName1")

        connector.getUnLocode(code).futureValue.value mustEqual expectedResult
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getUnLocode(code))
      }
    }

    "getNationalities" - {
      val url = s"/$baseUrl/lists/Nationality"

      val nationalitiesResponseJson: String =
        """
            |[
            |  {
            |    "key": "GB",
            |    "value": "United Kingdom"
            |  },
            |  {
            |    "key": "AD",
            |    "value": "Andorra"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a sequence of Nationalities" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(nationalitiesResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          Nationality("GB", "United Kingdom"),
          Nationality("AD", "Andorra")
        )

        connector.getNationalities().futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getNationalities())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getNationalities())
      }
    }

    "getTypesOfLocation" - {
      val url = s"/$baseUrl/lists/TypeOfLocation"

      val typeOfLocationResponseJson: String =
        """
            |[
            |  {
            |    "key": "A",
            |    "value": "Designated location"
            |  },
            |  {
            |    "key": "B",
            |    "value": "Authorised place"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a sequence of LocationType" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(typeOfLocationResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          TypeOfLocation("A", "Designated location"),
          TypeOfLocation("B", "Authorised place")
        )

        connector.getTypesOfLocation().futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getTypesOfLocation())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getTypesOfLocation())
      }
    }

    "getIdentifications" - {
      val url = s"/$baseUrl/lists/QualifierOfTheIdentification"

      val identifiersResponseJson: String =
        """
            |[
            |  {
            |    "key": "U",
            |    "value": "UN/LOCODE"
            |  },
            |  {
            |    "key": "W",
            |    "value": "GPS coordinates"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a sequence of Identifications" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(identifiersResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          QualifierOfIdentification("U", "UN/LOCODE"),
          QualifierOfIdentification("W", "GPS coordinates")
        )
        connector.getIdentifications().futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getIdentifications())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getIdentifications())
      }
    }

    "getCountriesWithoutZip" - {
      val url = s"/$baseUrl/lists/CountryWithoutZip"

      val countryCodesResponseJson: String =
        """
            |[
            |  {
            |    "key": "GB"
            |  },
            |  {
            |    "key": "AD"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a sequence of country codes" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(countryCodesResponseJson))
        )

        val expectedResult = NonEmptySet.of(
          CountryCode("GB"),
          CountryCode("AD")
        )

        connector.getCountriesWithoutZip().futureValue.value mustEqual expectedResult
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getCountriesWithoutZip())
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountriesWithoutZip())
      }
    }

    "getCountryWithoutZip" - {
      val countryCode = CountryCode("GB")

      val url = s"/$baseUrl/lists/CountryWithoutZip?keys=${countryCode.code}"

      val countryCodeResponseJson: String =
        """
            |[
            |  {
            |    "key": "GB"
            |  }
            |]
            |""".stripMargin

      "must return a successful future response with a country code" in {
        server.stubFor(
          get(urlEqualTo(url))
            .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
            .willReturn(okJson(countryCodeResponseJson))
        )

        connector.getCountryWithoutZip(countryCode).futureValue.value mustEqual countryCode
      }

      "must throw a NoReferenceDataFoundException for an empty response" in {
        checkNoReferenceDataFoundResponse(url, emptyResponseJson, connector.getCountryWithoutZip(countryCode))
      }

      "must return an exception when an error response is returned" in {
        checkErrorResponse(url, connector.getCountryWithoutZip(countryCode))
      }
    }
  }

  private def checkNoReferenceDataFoundResponse(url: String, json: String, result: => Future[Either[Exception, ?]]): Assertion = {
    server.stubFor(
      get(urlEqualTo(url))
        .willReturn(okJson(json))
    )

    result.futureValue.left.value mustBe a[NoReferenceDataFoundException]
  }

  private def checkErrorResponse(url: String, result: => Future[Either[Exception, ?]]): Assertion = {
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

        result.futureValue.left.value mustBe an[Exception]
    }
  }
}
