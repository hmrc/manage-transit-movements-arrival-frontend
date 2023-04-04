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

package api

import base.SpecBase
import generated._
import models.UserAnswers
import play.api.libs.json.{JsValue, Json}

class DestinationDetailsSpec extends SpecBase {

  "DestinationDetails" - {

    val json: JsValue = Json.parse(s"""
         |{
         |  "_id" : "c8fdf8a7-1c77-4d25-991d-2a0881e05062",
         |  "mrn" : "${mrn.toString}",
         |  "eoriNumber" : "GB1234567",
         |  "data" : {
         |    "identification" : {
         |      "destinationOffice" : {
         |        "id" : "GB000051",
         |        "name" : "Felixstowe",
         |        "phoneNumber" : "+44 (0)1394 303023 / 24 / 26"
         |      },
         |      "identificationNumber" : "GB123456789000",
         |      "isSimplifiedProcedure" : "normal",
         |      "authorisations" : [
         |        {
         |          "typeValue" : "SSE",
         |          "referenceNumber" : "SSE1"
         |        },
         |        {
         |          "typeValue" : "ACR",
         |          "referenceNumber" : "ACR1"
         |        }
         |      ]
         |    },
         |    "locationOfGoods" : {
         |      "typeOfLocation" : "authorisedPlace",
         |      "qualifierOfIdentification" : "customsOffice",
         |      "qualifierOfIdentificationDetails" : {
         |        "customsOffice" : {
         |          "id" : "GB000142",
         |          "name" : "Belfast EPU",
         |          "phoneNumber" : "+44 (0)3000 523068"
         |        }
         |      }
         |    },
         |    "incidentFlag" : true,
         |    "incidents" : [
         |      {
         |        "incidentCountry" : {
         |          "code" : "GB",
         |          "description" : "United Kingdom"
         |        },
         |        "incidentCode" : "partiallyOrFullyUnloaded",
         |        "incidentText" : "foo",
         |        "addEndorsement" : true,
         |        "endorsement" : {
         |          "date" : "2023-01-01",
         |          "authority" : "bar",
         |          "country" : {
         |            "code" : "GB",
         |            "description" : "United Kingdom"
         |          },
         |          "location" : "foobar"
         |        },
         |        "qualifierOfIdentification" : "unlocode",
         |        "unLocode" : {
         |          "unLocodeExtendedCode" : "ADCAN",
         |          "name" : "Canillo"
         |        },
         |        "equipments" : [
         |          {
         |            "containerIdentificationNumberYesNo" : true,
         |            "containerIdentificationNumber" : "1",
         |            "addSealsYesNo" : true,
         |            "seals" : [
         |              {
         |                "sealIdentificationNumber" : "1"
         |              }
         |            ],
         |            "addGoodsItemNumberYesNo" : true,
         |            "itemNumbers" : [
         |              {
         |                "itemNumber" : "1"
         |              }
         |            ]
         |          }
         |        ]
         |      }
         |    ]
         |  },
         |  "lastUpdated" : {
         |    "$$date" : {
         |      "$$numberLong" : "1662546803472"
         |    }
         |  }
         |}
         |""".stripMargin)

    "customsOfficeOfDestination is called" - {

      "convert to API format" in {

        val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

        val converted = DestinationDetails.customsOfficeOfDestination(uA)

        val expected = CustomsOfficeOfDestinationActualType03(
          referenceNumber = "GB000051"
        )

        converted mustBe expected
      }

    }

    "traderAtDestination is called" - {

      "convert to API format" in {

        val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

        val converted = DestinationDetails.traderAtDestination(uA)

        val expected = TraderAtDestinationType01(
          identificationNumber = "GB123456789000"
        )

        converted mustBe expected
      }

    }
  }
}
