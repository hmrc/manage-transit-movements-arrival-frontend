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

  private lazy val phase5App: GuiceApplicationBuilder => GuiceApplicationBuilder =
    _ => guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> false)

  private lazy val phase6App: GuiceApplicationBuilder => GuiceApplicationBuilder =
    _ => guiceApplicationBuilder().configure("feature-flags.phase-6-enabled" -> true)

  private val emptyPhase5ResponseJson: String =
    """
      |{
      |  "data": []
      |}
      |""".stripMargin

  private val emptyPhase6ResponseJson: String =
    """
      |[]
      |""".stripMargin

  "Reference Data" - {

    "getCustomsOfficesForCountry" - {
      val countryIds = Seq("GB", "XI")

      "when phase 5" - {

        val url = s"/$baseUrl/lists/CustomsOffices?data.countryId=GB&data.countryId=XI&data.roles.role=DES"

        val customsOfficesResponseJson: String =
          """
            |{
            |  "data": [
            |    {
            |      "id" : "GBtestId1",
            |      "languageCode": "EN",
            |      "name" : "testName1",
            |      "roles" : ["DES"],
            |      "phoneNumber" : "testPhoneNumber",
            |      "countryId" : "GB"
            |    },
            |    {
            |      "id" : "GBtestId2",
            |      "languageCode": "EN",
            |      "name" : "testName2",
            |      "roles" : ["DES"],
            |      "countryId" : "GB"
            |    }
            |  ]
            |}
            |""".stripMargin

        "must return a successful future response with a sequence of CustomsOffices" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(customsOfficesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                CustomsOffice("GBtestId1", "testName1", Some("testPhoneNumber"), "GB"),
                CustomsOffice("GBtestId2", "testName2", None, "GB")
              )

              connector.getCustomsOfficesForCountry(countryIds*).futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "AR"
              val url       = s"/$baseUrl/lists/CustomsOffices?data.countryId=AR&data.roles.role=DES"
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getCustomsOfficesForCountry(countryId))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              val countryId = "GB"
              val url       = s"/$baseUrl/lists/CustomsOffices?data.countryId=GB&data.roles.role=DES"
              checkErrorResponse(url, connector.getCustomsOfficesForCountry(countryId))
          }
        }
      }
    }

    "getCountries" - {

      "when phase 5" - {

        val countriesResponseJson: String =
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

        "for a given list name" - {
          "must return Seq of Country when successful" in {
            running(phase5App) {
              app =>
                val connector = app.injector.instanceOf[ReferenceDataConnector]
                forAll(Gen.alphaNumStr) {
                  listName =>
                    server.stubFor(
                      get(urlEqualTo(s"/$baseUrl/lists/$listName"))
                        .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                        .willReturn(okJson(countriesResponseJson))
                    )

                    val expectedResult = NonEmptySet.of(
                      Country(CountryCode("GB"), "United Kingdom"),
                      Country(CountryCode("AD"), "Andorra")
                    )

                    connector.getCountries(listName).futureValue.value mustEqual expectedResult
                }
            }
          }

          "must throw a NoReferenceDataFoundException for an empty response" in {
            running(phase5App) {
              app =>
                val connector = app.injector.instanceOf[ReferenceDataConnector]
                forAll(Gen.alphaNumStr) {
                  listName =>
                    checkNoReferenceDataFoundResponse(s"/$baseUrl/lists/$listName", emptyPhase5ResponseJson, connector.getCountries(listName))
                }
            }
          }

          "must return an exception when an error response is returned" in {
            running(phase5App) {
              app =>
                val connector = app.injector.instanceOf[ReferenceDataConnector]
                forAll(Gen.alphaNumStr) {
                  listName =>
                    checkErrorResponse(s"/$baseUrl/lists/$listName", connector.getCountries(listName))
                }
            }
          }
        }
      }

      "when phase 6" - {

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
            running(phase6App) {
              app =>
                val connector = app.injector.instanceOf[ReferenceDataConnector]
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
          }

          "must throw a NoReferenceDataFoundException for an empty response" in {
            running(phase6App) {
              app =>
                val connector = app.injector.instanceOf[ReferenceDataConnector]
                forAll(Gen.alphaNumStr) {
                  listName =>
                    checkNoReferenceDataFoundResponse(s"/$baseUrl/lists/$listName", emptyPhase6ResponseJson, connector.getCountries(listName))
                }
            }
          }

          "must return an exception when an error response is returned" in {
            running(phase6App) {
              app =>
                val connector = app.injector.instanceOf[ReferenceDataConnector]
                forAll(Gen.alphaNumStr) {
                  listName =>
                    checkErrorResponse(s"/$baseUrl/lists/$listName", connector.getCountries(listName))
                }
            }
          }
        }
      }
    }

    "getUnLocodes" - {
      val url = s"/$baseUrl/lists/UnLocodeExtended"

      "when phase 5" - {

        val unlocodesResponseJson: String =
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

        "must return a successful future response with a sequence of UnLocodes" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(unlocodesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                UnLocode("code1", "name1"),
                UnLocode("code2", "name2")
              )

              connector.getUnLocodes().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getUnLocodes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getUnLocodes())
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
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
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getUnLocodes())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getUnLocodes())
          }
        }
      }
    }

    "getUnLocode" - {
      val code = "UN1"

      "when phase 5" - {

        val url = s"/$baseUrl/lists/UnLocodeExtended?data.unLocodeExtendedCode=UN1"

        val unLocodeResponseJson: String =
          """
            |{
            |  "_links": {
            |    "self": {
            |      "href": "/customs-reference-data/lists/UnLocodeExtended"
            |    }
            |  },
            |  "meta": {
            |    "version": "410157ad-bc37-4e71-af2a-404d1ddad94c",
            |    "snapshotDate": "2023-01-01"
            |  },
            |  "id": "UnLocodeExtended",
            |  "data": [
            |    {
            |      "state": "valid",
            |      "activeFrom": "2019-01-01",
            |      "unLocodeExtendedCode": "UN1",
            |      "name": "testName1"
            |    }
            |  ]
            |}
            |""".stripMargin

        "must return a Seq of UN/LOCODES when successful" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(unLocodeResponseJson))
              )

              val expectedResult = UnLocode("UN1", "testName1")

              connector.getUnLocode(code).futureValue.value mustEqual expectedResult
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getUnLocode(code))
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(unLocodeResponseJson))
              )

              val expectedResult = UnLocode("UN1", "testName1")

              connector.getUnLocode(code).futureValue.value mustEqual expectedResult
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getUnLocode(code))
          }
        }
      }
    }

    "getNationalities" - {
      val url = s"/$baseUrl/lists/Nationality"

      "when phase 5" - {

        val nationalitiesResponseJson: String =
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

        "must return a successful future response with a sequence of Nationalities" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(nationalitiesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                Nationality("GB", "United Kingdom"),
                Nationality("AD", "Andorra")
              )

              connector.getNationalities().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getNationalities())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getNationalities())
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
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
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getNationalities())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getNationalities())
          }
        }
      }
    }

    "getTypesOfLocation" - {
      val url = s"/$baseUrl/lists/TypeOfLocation"

      "when phase 5" - {

        val typeOfLocationResponseJson: String =
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

        "must return a successful future response with a sequence of LocationType" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(typeOfLocationResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                TypeOfLocation("A", "Designated location"),
                TypeOfLocation("B", "Authorised place")
              )

              connector.getTypesOfLocation().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getTypesOfLocation())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getTypesOfLocation())
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
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
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getTypesOfLocation())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getTypesOfLocation())
          }
        }
      }
    }

    "getIdentifications" - {
      val url = s"/$baseUrl/lists/QualifierOfTheIdentification"

      "when phase 5" - {

        val identifiersResponseJson: String =
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

        "must return a successful future response with a sequence of Identifications" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(identifiersResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                QualifierOfIdentification("U", "UN/LOCODE"),
                QualifierOfIdentification("W", "GPS coordinates")
              )
              connector.getIdentifications().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getIdentifications())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getIdentifications())
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
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
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getIdentifications())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getIdentifications())
          }
        }
      }
    }

    "getCountriesWithoutZip" - {
      val url = s"/$baseUrl/lists/CountryWithoutZip"

      "when phase 5" - {

        val countryCodesResponseJson: String =
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

        "must return a successful future response with a sequence of country codes" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countryCodesResponseJson))
              )

              val expectedResult = NonEmptySet.of(
                CountryCode("GB"),
                CountryCode("AD")
              )

              connector.getCountriesWithoutZip().futureValue.value mustEqual expectedResult
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getCountriesWithoutZip())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountriesWithoutZip())
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
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
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getCountriesWithoutZip())
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountriesWithoutZip())
          }
        }
      }
    }

    "getCountryWithoutZip" - {
      val countryCode = CountryCode("GB")

      "when phase 5" - {

        val url = s"/$baseUrl/lists/CountryWithoutZip?data.code=${countryCode.code}"

        val countryCodeResponseJson: String =
          """
            |{
            |  "data": [
            |    {
            |      "code": "GB"
            |    }
            |  ]
            |}
            |""".stripMargin

        "must return a successful future response with a country code" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.1.0+json"))
                  .willReturn(okJson(countryCodeResponseJson))
              )

              connector.getCountryWithoutZip(countryCode).futureValue.value mustEqual countryCode
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase5ResponseJson, connector.getCountryWithoutZip(countryCode))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase5App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountryWithoutZip(countryCode))
          }
        }
      }

      "when phase 6" - {

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
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              server.stubFor(
                get(urlEqualTo(url))
                  .withHeader("Accept", equalTo("application/vnd.hmrc.2.0+json"))
                  .willReturn(okJson(countryCodeResponseJson))
              )

              connector.getCountryWithoutZip(countryCode).futureValue.value mustEqual countryCode
          }
        }

        "must throw a NoReferenceDataFoundException for an empty response" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkNoReferenceDataFoundResponse(url, emptyPhase6ResponseJson, connector.getCountryWithoutZip(countryCode))
          }
        }

        "must return an exception when an error response is returned" in {
          running(phase6App) {
            app =>
              val connector = app.injector.instanceOf[ReferenceDataConnector]
              checkErrorResponse(url, connector.getCountryWithoutZip(countryCode))
          }
        }
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
