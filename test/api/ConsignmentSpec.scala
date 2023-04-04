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

class ConsignmentSpec extends SpecBase {

  "Consignment" - {

    "transform is called" - {

      "locationOfGoods qualifierOfIdentification" - {

        "for customs office" - {

          "convert to API format" in {

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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "V",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = Some(CustomsOfficeType01("GB000142")),
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = None
                )
              )
            )

            converted mustBe expected

          }
        }

        "for eori number" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "eoriNumber",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "identificationNumber" : "GB123456789000",
                 |        "addAdditionalIdentifier" : true,
                 |        "additionalIdentifier" : "0000"
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "X",
                authorisationNumber = None,
                additionalIdentifier = Some("0000"),
                UNLocode = None,
                CustomsOffice = None,
                GNSS = None,
                EconomicOperator = Some(EconomicOperatorType03("GB123456789000")),
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

        "for authorisation number" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "authorisationNumber",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "authorisationNumber" : "GB123456789000",
                 |        "addAdditionalIdentifier" : true,
                 |        "additionalIdentifier" : "0000"
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "Y",
                authorisationNumber = Some("GB123456789000"),
                additionalIdentifier = Some("0000"),
                UNLocode = None,
                CustomsOffice = None,
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

        "for coordinates number" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "coordinates",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "coordinates": {
                 |          "latitude": "50.96622",
                 |          "longitude": "1.86201"
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "W",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = None,
                GNSS = Some(
                  GNSSType(
                    latitude = "50.96622",
                    longitude = "1.86201"
                  )
                ),
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

        "for unlocode number" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "unlocode",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "unlocode": {
                 |          "unLocodeExtendedCode": "DEAAL",
                 |          "name": "Alean"
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "U",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = Some("DEAAL"),
                CustomsOffice = None,
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

        "for address" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "address",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "country": {
                 |          "code": "FR",
                 |          "description": "France"
                 |        }
                 |        ,
                 |        "address": {
                 |          "numberAndStreet": "28 Poker Avenue",
                 |          "city": "Foo",
                 |          "postalCode": "NEXX XXX"
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "Z",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = None,
                GNSS = None,
                EconomicOperator = None,
                Address = Some(
                  AddressType14(
                    streetAndNumber = "28 Poker Avenue",
                    postcode = Some("NEXX XXX"),
                    city = "Foo",
                    country = "FR"
                  )
                ),
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

        "for address (no postcode)" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "address",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "country": {
                 |          "code": "FR",
                 |          "description": "France"
                 |        }
                 |        ,
                 |        "address": {
                 |          "numberAndStreet": "28 Poker Avenue",
                 |          "city": "Foo"
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "Z",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = None,
                GNSS = None,
                EconomicOperator = None,
                Address = Some(
                  AddressType14(
                    streetAndNumber = "28 Poker Avenue",
                    postcode = None,
                    city = "Foo",
                    country = "FR"
                  )
                ),
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

        "for postcode" - {

          "convert to API format" in {

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
                 |      "qualifierOfIdentification" : "postalCode",
                 |      "qualifierOfIdentificationDetails" : {
                 |        "postalCode": {
                 |          "streetNumber": "28",
                 |          "postalCode": "NXX XXX",
                 |          "country": {
                 |            "code": "FR",
                 |            "description": "France"
                 |          }
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
                 |        ],
                 |        "transportMeans" : {
                 |          "identification" : "seaGoingVessel",
                 |          "identificationNumber" : "foo",
                 |          "transportNationality" : {
                 |            "code" : "FR",
                 |            "desc" : "France"
                 |          }
                 |        }
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "T",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = None,
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = Some(
                  PostcodeAddressType02(
                    houseNumber = Some("28"),
                    postcode = "NXX XXX",
                    country = "FR"
                  )
                ),
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = Some(
                    TranshipmentType01(
                      containerIndicator = Number0,
                      TransportMeans = TransportMeansType01(
                        typeOfIdentification = "11",
                        identificationNumber = "foo",
                        nationality = "FR"
                      )
                    )
                  )
                )
              )
            )

            converted mustBe expected

          }
        }

      }

      "Incident qualifierOfIdentification" - {

        "for unlocode" - {

          "convert to API format" in {

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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "V",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = Some(CustomsOfficeType01("GB000142")),
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "U",
                    UNLocode = Some("ADCAN"),
                    country = "GB",
                    GNSS = None,
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = None
                )
              )
            )

            converted mustBe expected

          }
        }

        "for coordinates" - {

          "convert to API format" in {

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
                 |        "qualifierOfIdentification" : "coordinates",
                 |        "coordinates" : {
                 |          "latitude" : "12345",
                 |          "longitude" : "54321"
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "V",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = Some(CustomsOfficeType01("GB000142")),
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "W",
                    UNLocode = None,
                    country = "GB",
                    GNSS = Some(
                      GNSSType(
                        latitude = "12345",
                        longitude = "54321"
                      )
                    ),
                    Address = None
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = None
                )
              )
            )

            converted mustBe expected

          }
        }

        "for address" - {

          "convert to API format" in {

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
                 |        "qualifierOfIdentification" : "address",
                 |        "address" : {
                 |          "numberAndStreet" : "24 Poker Avenue",
                 |          "city" : "Foo",
                 |          "postalCode" : "NEX XXX"
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

            val uA: UserAnswers = json.as[UserAnswers](UserAnswers.format)

            val converted = Consignment.transform(uA)

            val expected = ConsignmentType01(
              LocationOfGoods = LocationOfGoodsType01(
                typeOfLocation = "B",
                qualifierOfIdentification = "V",
                authorisationNumber = None,
                additionalIdentifier = None,
                UNLocode = None,
                CustomsOffice = Some(CustomsOfficeType01("GB000142")),
                GNSS = None,
                EconomicOperator = None,
                Address = None,
                PostcodeAddress = None,
                ContactPerson = None
              ),
              Incident = Seq(
                IncidentType01(
                  sequenceNumber = "1",
                  code = "4",
                  text = "foo",
                  Endorsement = Some(
                    EndorsementType01(
                      date = converted.Incident
                        .flatMap(
                          x =>
                            x.Endorsement.map(
                              y => y.date
                            )
                        )
                        .head,
                      authority = "bar",
                      place = "foobar",
                      country = "GB"
                    )
                  ),
                  Location = LocationType01(
                    qualifierOfIdentification = "Z",
                    UNLocode = None,
                    country = "GB",
                    GNSS = None,
                    Address = Some(
                      AddressType01(
                        streetAndNumber = "24 Poker Avenue",
                        postcode = Some("NEX XXX"),
                        city = "Foo"
                      )
                    )
                  ),
                  TransportEquipment = Seq(
                    TransportEquipmentType01(
                      sequenceNumber = "1",
                      containerIdentificationNumber = Some("1"),
                      numberOfSeals = Some(BigInt(1)),
                      Seal = Seq(
                        SealType05(sequenceNumber = "1", identifier = "1")
                      ),
                      GoodsReference = Seq(
                        GoodsReferenceType01(
                          sequenceNumber = "1",
                          declarationGoodsItemNumber = BigInt(1)
                        )
                      )
                    )
                  ),
                  Transhipment = None
                )
              )
            )

            converted mustBe expected

          }
        }
      }
    }
  }
}
