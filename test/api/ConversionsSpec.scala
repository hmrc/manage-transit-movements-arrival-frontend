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

package api

import base.{AppWithDefaultMockFixtures, SpecBase}
import generated._
import generators.Generators
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.incident.IncidentCode
import models.incident.transportMeans.Identification
import models.journeyDomain.ArrivalPostTransitionDomain
import models.journeyDomain.identification.{AuthorisationDomain, AuthorisationsDomain, IdentificationDomain}
import models.journeyDomain.incident.endorsement.EndorsementDomain
import models.journeyDomain.incident.equipment.itemNumber.ItemNumbersDomain
import models.journeyDomain.incident.equipment.seal.{SealDomain, SealsDomain}
import models.journeyDomain.incident.equipment.{EquipmentDomain, EquipmentsDomain}
import models.journeyDomain.incident.{IncidentDomain, IncidentUnLocodeLocationDomain, IncidentsDomain, TransportMeansDomain}
import models.journeyDomain.locationOfGoods.{AddressDomain, LocationOfGoodsDomain}
import models.locationOfGoods.TypeOfLocation.AuthorisedPlace
import models.reference.{Country, CustomsOffice, Nationality, UnLocode}
import models.{DynamicAddress, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident.ContainerIndicatorYesNoPage

import java.time.LocalDate

class ConversionsSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  val uA: UserAnswers = arbitraryArrivalAnswers(emptyUserAnswers).sample.value

  "Conversions" - {

    val destinationOffice: CustomsOffice = arbitrary[CustomsOffice].sample.value

    val identificationDomain = IdentificationDomain(
      mrn = uA.mrn,
      destinationOffice = destinationOffice,
      identificationNumber = "identificationNumber",
      procedureType = ProcedureType.Normal,
      authorisations = None
    )

    "message is called" - {

      "will convert to API format" in {

        val converted = Conversions.message

        val expected = MESSAGE_FROM_TRADERSequence(
          None,
          MESSAGE_1Sequence(
            messageRecipient = "NCTS",
            preparationDateAndTime = converted.messagE_1Sequence2.preparationDateAndTime,
            messageIdentification = "CC007C"
          )
        )

        converted mustBe expected

      }

    }

    "messageType is called" - {

      "will convert to API format" in {

        Conversions.messageType.toString mustBe "CC007C"

      }

    }

    "transitOperation is called" - {

      "will convert to API format" in {

        val hasIncidents: Boolean             = true
        val converted: TransitOperationType02 = Conversions.transitOperation(identificationDomain, hasIncidents)

        val expected = TransitOperationType02(
          MRN = uA.mrn.toString,
          arrivalNotificationDateAndTime = converted.arrivalNotificationDateAndTime,
          simplifiedProcedure = ApiXmlHelpers.boolToFlag(false),
          incidentFlag = ApiXmlHelpers.boolToFlag(hasIncidents)
        )

        converted mustBe expected

      }

    }

    "authorisations is called" - {

      "will convert to API format where authorisations" in {

        val authorisationType = arbitrary[AuthorisationType].sample.value
        val referenceNumber   = Gen.alphaNumStr.sample.value

        val domain = AuthorisationsDomain(
          authorisations = Seq(
            AuthorisationDomain(
              `type` = authorisationType,
              referenceNumber = referenceNumber
            )(authorisationIndex)
          )
        )

        val converted = Conversions.authorisations(Some(domain))

        val expected = Seq(
          AuthorisationType01(authorisationIndex.position.toString, authorisationType.toString, referenceNumber)
        )

        converted mustBe expected

      }

      "will convert to API format where no authorisations" in {

        val domain: AuthorisationsDomain        = AuthorisationsDomain(authorisations = Seq.empty)
        val converted: Seq[AuthorisationType01] = Conversions.authorisations(Some(domain))

        converted mustBe Seq.empty

      }

    }

    "customsOfficeOfDestination is called" - {

      "will convert to API format" in {

        val destinationOffice: CustomsOffice                 = arbitrary[CustomsOffice].sample.value
        val expected: CustomsOfficeOfDestinationActualType03 = CustomsOfficeOfDestinationActualType03(destinationOffice.id)

        Conversions.customsOfficeOfDestination(destinationOffice) mustBe expected

      }

    }

    "traderAtDestination ic called" - {

      "will convert to API format" in {

        val expected: TraderAtDestinationType01 = TraderAtDestinationType01("identificationNumber", None)

        Conversions.traderAtDestination(identificationDomain) mustBe expected

      }

    }

    "consignment is called" - {

      val country: Country               = arbitrary[Country].sample.value
      val address: DynamicAddress        = arbitrary[DynamicAddress].sample.value
      val incidentCode: IncidentCode     = arbitrary[IncidentCode].sample.value
      val localDate: LocalDate           = LocalDate.now()
      val authority: String              = Gen.alphaNumStr.sample.value
      val location: String               = Gen.alphaNumStr.sample.value
      val unLocode: UnLocode             = arbitrary[UnLocode].sample.value
      val containerId: String            = Gen.alphaNumStr.sample.value
      val sealId: String                 = Gen.alphaNumStr.sample.value
      val identification: Identification = arbitrary[Identification].sample.value
      val identificationNumber: String   = Gen.alphaNumStr.sample.value
      val nationality: Nationality       = arbitrary[Nationality].sample.value
      val containerIndicator: Boolean    = uA.get(ContainerIndicatorYesNoPage(incidentIndex)).isDefined

      val locationOfGoodsDomain: LocationOfGoodsDomain = LocationOfGoodsDomain(
        typeOfLocation = AuthorisedPlace,
        qualifierOfIdentificationDetails = AddressDomain(
          country = country,
          address = address,
          contactPerson = None
        )
      )

      val equipment: EquipmentDomain = EquipmentDomain(
        Some(containerId),
        SealsDomain(Seq(SealDomain(sealId)(incidentIndex, equipmentIndex, sealIndex))),
        ItemNumbersDomain(Nil)
      )(incidentIndex, equipmentIndex)

      val transportMeansDomain: TransportMeansDomain = TransportMeansDomain(
        identificationType = identification,
        identificationNumber = identificationNumber,
        nationality = nationality
      )

      val equipments: EquipmentsDomain                   = EquipmentsDomain(Seq(equipment))(equipmentIndex)
      val locationDomain: IncidentUnLocodeLocationDomain = IncidentUnLocodeLocationDomain(unLocode = unLocode)
      val endorsement: EndorsementDomain                 = EndorsementDomain(localDate, authority, country, location)
      val incidentText                                   = "Some Incident"
      val incident: IncidentDomain =
        IncidentDomain(country, incidentCode, incidentText, Some(endorsement), locationDomain, equipments, Some(transportMeansDomain))(incidentIndex)
      val incidentsDomain: IncidentsDomain = IncidentsDomain(Seq(incident))

      val domain = ArrivalPostTransitionDomain(
        identificationDomain,
        locationOfGoodsDomain,
        Some(incidentsDomain)
      )

      "will convert to API format" in {

        val converted = Conversions.consignment(domain, uA)

        val expected: ConsignmentType01 = ConsignmentType01(
          LocationOfGoodsType01(
            AuthorisedPlace.code,
            locationOfGoodsDomain.qualifierOfIdentificationDetails.qualifierOfIdentification,
            None,
            None,
            None,
            None,
            None,
            None,
            Some(AddressType14(address.numberAndStreet, address.postalCode, address.city, country.code.code)),
            None,
            None
          ),
          List(
            IncidentType01(
              incidentIndex.position.toString,
              incidentCode.code,
              incidentText,
              Some(
                EndorsementType01(
                  converted.Incident
                    .flatMap(
                      i =>
                        i.Endorsement.map(
                          e => e.date
                        )
                    )
                    .head,
                  authority,
                  location,
                  country.code.code
                )
              ),
              LocationType01(locationDomain.code, Some(unLocode.unLocodeExtendedCode), country.code.code, None, None),
              List(
                TransportEquipmentType01(
                  equipmentIndex.position.toString,
                  Some(containerId),
                  Some(1),
                  List(SealType05(equipmentIndex.position.toString, sealId)),
                  List()
                )
              ),
              Some(
                TranshipmentType01(ApiXmlHelpers.boolToFlag(containerIndicator),
                                   TransportMeansType01(identification.code, identificationNumber, nationality.code)
                )
              )
            )
          )
        )

        converted mustBe expected

      }

    }

  }

}
