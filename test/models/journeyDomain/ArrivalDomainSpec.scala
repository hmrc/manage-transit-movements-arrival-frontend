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

package models.journeyDomain

import base.SpecBase
import forms.Constants
import generators.Generators
import models.InternationalAddress
import models.journeyDomain.identification.{AuthorisationsDomain, IdentificationDomain}
import models.journeyDomain.incident.{IncidentDomain, IncidentDomainList}
import models.journeyDomain.locationOfGoods.{AddressDomain, LocationOfGoodsDomain}
import models.locationOfGoods.QualifierOfIdentification
import models.locationOfGoods.TypeOfLocation.AuthorisedPlace
import models.reference.{Country, CountryCode, IncidentCode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.{ArrivalDatePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import pages.incident.{IncidentCodePage, IncidentCountryPage, IncidentFlagPage, IncidentTextPage}
import pages.locationOfGoods.{AddContactPersonPage, InternationalAddressPage, QualifierOfIdentificationPage, TypeOfLocationPage}

import java.time.LocalDate

class ArrivalDomainSpec extends SpecBase with Generators {

  private val country      = arbitrary[Country].sample.value
  private val incidentCode = arbitrary[IncidentCode].sample.value
  private val incidentText = Gen.alphaNumStr.sample.value.take(Constants.maxIncidentTextLength)
  private val date         = arbitrary[LocalDate].sample.value
  private val id           = Gen.alphaNumStr.sample.value

  "ArrivalDomain" - {

    "can be parsed from UserAnswers with Incidents" in {

      val userAnswers = emptyUserAnswers
        .setValue(ArrivalDatePage, date)
        .setValue(IsSimplifiedProcedurePage, false)
        .setValue(IdentificationNumberPage, id)
        .setValue(TypeOfLocationPage, AuthorisedPlace)
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
        .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
        .setValue(AddContactPersonPage, false)
        .setValue(IncidentFlagPage, true)
        .setValue(IncidentCountryPage(index), country)
        .setValue(IncidentCodePage(index), incidentCode)
        .setValue(IncidentTextPage(index), incidentText)

      val expectedResult = ArrivalDomain(
        IdentificationDomain(
          userAnswers.mrn,
          arrivalDate = date,
          isSimplified = false,
          authorisations = AuthorisationsDomain(
            Seq.empty
          ),
          identificationNumber = id
        ),
        LocationOfGoodsDomain(
          typeOfLocation = AuthorisedPlace,
          qualifierOfIdentificationDetails = AddressDomain(
            address = InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
            contactPerson = None
          )
        ),
        Some(
          IncidentDomainList(
            Seq(
              IncidentDomain(
                incidentCountry = country,
                incidentCode = incidentCode,
                incidentText = incidentText
              )
            )
          )
        )
      )

      val result: EitherType[ArrivalDomain] = UserAnswersReader[ArrivalDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers with no Incidents" in {

      val userAnswers = emptyUserAnswers
        .setValue(ArrivalDatePage, date)
        .setValue(IsSimplifiedProcedurePage, false)
        .setValue(IdentificationNumberPage, id)
        .setValue(TypeOfLocationPage, AuthorisedPlace)
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
        .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
        .setValue(AddContactPersonPage, false)
        .setValue(IncidentFlagPage, false)

      val expectedResult = ArrivalDomain(
        IdentificationDomain(
          userAnswers.mrn,
          arrivalDate = date,
          isSimplified = false,
          authorisations = AuthorisationsDomain(
            Seq.empty
          ),
          identificationNumber = id
        ),
        LocationOfGoodsDomain(
          typeOfLocation = AuthorisedPlace,
          qualifierOfIdentificationDetails = AddressDomain(
            address = InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
            contactPerson = None
          )
        ),
        None
      )

      val result: EitherType[ArrivalDomain] = UserAnswersReader[ArrivalDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "cannot be parsed from UserAnswer" - {

      "when a incident flag page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(ArrivalDatePage, date)
          .setValue(IsSimplifiedProcedurePage, false)
          .setValue(IdentificationNumberPage, id)
          .setValue(TypeOfLocationPage, AuthorisedPlace)
          .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
          .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .setValue(AddContactPersonPage, false)
          .setValue(IncidentCountryPage(index), country)
          .setValue(IncidentCodePage(index), incidentCode)
          .setValue(IncidentTextPage(index), incidentText)

        val result: EitherType[ArrivalDomain] = UserAnswersReader[ArrivalDomain].run(userAnswers)

        result.left.value.page mustBe IncidentFlagPage

      }
    }
  }

}