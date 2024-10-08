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

package models.journeyDomain

import base.{AppWithDefaultMockFixtures, SpecBase}
import config.Constants.IncidentCode._
import config.Constants.QualifierCode._
import config.PhaseConfig
import generators.Generators
import models.identification.ProcedureType
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.incident.equipment.itemNumber.{ItemNumberDomain, ItemNumbersDomain}
import models.journeyDomain.incident.equipment.seal.{SealDomain, SealsDomain}
import models.journeyDomain.incident.equipment.{EquipmentDomain, EquipmentsDomain}
import models.journeyDomain.incident.{IncidentDomain, IncidentUnLocodeLocationDomain, IncidentsDomain, TransportMeansDomain}
import models.journeyDomain.locationOfGoods.{AddressDomain, LocationOfGoodsDomain}
import models.reference._
import models.{DynamicAddress, Phase}
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.{DestinationOfficePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import pages.incident._
import pages.incident.equipment.itemNumber.ItemNumberPage
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.incident.equipment.{AddGoodsItemNumberYesNoPage, AddSealsYesNoPage, ContainerIdentificationNumberPage}
import pages.incident.location.{QualifierOfIdentificationPage => IncidentQualifierOfIdentificationPage, UnLocodePage}
import pages.incident.transportMeans.{IdentificationNumberPage => TransportMeansIdentificationNumberPage, IdentificationPage, TransportNationalityPage}
import pages.locationOfGoods._
import pages.sections.incident._

class ArrivalDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks with AppWithDefaultMockFixtures {

  private val destinationOffice       = arbitrary[CustomsOffice].sample.value
  private val country                 = arbitrary[Country].sample.value
  private val address                 = arbitrary[DynamicAddress].sample.value
  private val idNumber                = Gen.alphaNumStr.sample.value
  private val transportIdentification = arbitrary[Identification].sample.value
  private val typeOfLocation          = arbitrary[TypeOfLocation].sample.value

  "ArrivalDomain" - {

    "when post-transition" - {
      val mockPhaseConfig: PhaseConfig = mock[PhaseConfig]
      when(mockPhaseConfig.phase).thenReturn(Phase.PostTransition)

      "can be parsed from UserAnswers" in {

        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(IdentificationNumberPage, idNumber)
          .setValue(TypeOfLocationPage, typeOfLocation)
          .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(AddressCode).sample.value)
          .setValue(CountryPage, country)
          .setValue(AddressPage, address)
          .setValue(AddContactPersonPage, false)

        val expectedResult = ArrivalPostTransitionDomain(
          IdentificationDomain(
            userAnswers.mrn,
            destinationOffice = destinationOffice,
            identificationNumber = idNumber,
            procedureType = ProcedureType.Normal,
            authorisationReferenceNumber = None
          ),
          LocationOfGoodsDomain(
            typeOfLocation = Some(typeOfLocation),
            qualifierOfIdentificationDetails = AddressDomain(
              country = country,
              address = address,
              contactPerson = None
            )
          )
        )

        val result = ArrivalDomain.userAnswersReader(mockPhaseConfig).apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          DestinationOfficePage,
          IsSimplifiedProcedurePage,
          IdentificationNumberPage,
          TypeOfLocationPage,
          QualifierOfIdentificationPage,
          CountryPage,
          AddressPage,
          AddContactPersonPage
        )
      }
    }

    "when pre-transition" - {
      val mockPhaseConfig: PhaseConfig = mock[PhaseConfig]
      when(mockPhaseConfig.phase).thenReturn(Phase.Transition)

      "can be parsed from UserAnswers" in {

        val country     = arbitrary[Country].sample.value
        val nationality = arbitrary[Nationality].sample.value
        val text        = Gen.alphaNumStr.sample.value
        val unLocode    = arbitrary[String].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(IdentificationNumberPage, idNumber)
          .setValue(TypeOfLocationPage, typeOfLocation)
          .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(AddressCode).sample.value)
          .setValue(CountryPage, country)
          .setValue(AddressPage, address)
          .setValue(AddContactPersonPage, false)
          .setValue(IncidentFlagPage, true)
          .setValue(IncidentCountryPage(incidentIndex), country)
          .setValue(IncidentCodePage(incidentIndex), IncidentCode(UnexpectedlyChangedCode, "test"))
          .setValue(IncidentTextPage(incidentIndex), text)
          .setValue(AddEndorsementPage(incidentIndex), false)
          .setValue(IncidentQualifierOfIdentificationPage(incidentIndex), qualifierOfIdentificationGen(UnlocodeCode).sample.value)
          .setValue(UnLocodePage(incidentIndex), unLocode)
          .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)
          .setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), text)
          .setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), true)
          .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), text)
          .setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), true)
          .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), "1234")
          .setValue(IdentificationPage(incidentIndex), transportIdentification)
          .setValue(TransportMeansIdentificationNumberPage(incidentIndex), text)
          .setValue(TransportNationalityPage(incidentIndex), nationality)

        val expectedResult = ArrivalTransitionDomain(
          IdentificationDomain(
            userAnswers.mrn,
            destinationOffice = destinationOffice,
            identificationNumber = idNumber,
            procedureType = ProcedureType.Normal,
            authorisationReferenceNumber = None
          ),
          LocationOfGoodsDomain(
            typeOfLocation = Some(typeOfLocation),
            qualifierOfIdentificationDetails = AddressDomain(
              country = country,
              address = address,
              contactPerson = None
            )
          ),
          incidents = Some(
            IncidentsDomain(
              Seq(
                IncidentDomain(
                  incidentCountry = country,
                  incidentCode = IncidentCode(UnexpectedlyChangedCode, "test"),
                  incidentText = text,
                  endorsement = None,
                  location = IncidentUnLocodeLocationDomain(
                    unLocode = unLocode
                  ),
                  equipments = EquipmentsDomain(
                    Seq(
                      EquipmentDomain(
                        containerId = Some(text),
                        seals = SealsDomain(
                          Seq(
                            SealDomain(
                              identificationNumber = text
                            )(incidentIndex, equipmentIndex, sealIndex)
                          )
                        )(incidentIndex, equipmentIndex),
                        itemNumbers = ItemNumbersDomain(
                          Seq(
                            ItemNumberDomain(
                              itemNumber = "1234"
                            )(incidentIndex, equipmentIndex, itemNumberIndex)
                          )
                        )(incidentIndex, equipmentIndex)
                      )(incidentIndex, equipmentIndex)
                    )
                  )(incidentIndex),
                  transportMeans = Some(
                    TransportMeansDomain(
                      identificationType = transportIdentification,
                      identificationNumber = text,
                      nationality = nationality
                    )
                  )
                )(index)
              )
            )
          )
        )

        val result = ArrivalDomain.userAnswersReader(mockPhaseConfig).apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          DestinationOfficePage,
          IsSimplifiedProcedurePage,
          IdentificationNumberPage,
          TypeOfLocationPage,
          QualifierOfIdentificationPage,
          CountryPage,
          AddressPage,
          AddContactPersonPage,
          IncidentFlagPage,
          IncidentCountryPage(incidentIndex),
          IncidentCodePage(incidentIndex),
          IncidentTextPage(incidentIndex),
          AddEndorsementPage(incidentIndex),
          IncidentQualifierOfIdentificationPage(incidentIndex),
          UnLocodePage(incidentIndex),
          ContainerIndicatorYesNoPage(incidentIndex),
          ContainerIdentificationNumberPage(incidentIndex, equipmentIndex),
          AddSealsYesNoPage(incidentIndex, equipmentIndex),
          SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex),
          SealsSection(incidentIndex, equipmentIndex),
          AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex),
          ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex),
          ItemsSection(incidentIndex, equipmentIndex),
          EquipmentSection(incidentIndex, equipmentIndex),
          EquipmentsSection(incidentIndex),
          IdentificationPage(incidentIndex),
          TransportMeansIdentificationNumberPage(incidentIndex),
          TransportNationalityPage(incidentIndex),
          IncidentSection(incidentIndex),
          IncidentsSection
        )
      }
    }
  }
}
