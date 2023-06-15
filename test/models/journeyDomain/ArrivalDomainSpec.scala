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

import base.SpecBase
import generators.Generators
import models.identification.ProcedureType
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.locationOfGoods.{AddressDomain, LocationOfGoodsDomain}
import models.locationOfGoods.TypeOfLocation.AuthorisedPlace
import models.reference.{Country, CustomsOffice}
import models.{DynamicAddress, QualifierOfIdentification}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.{DestinationOfficePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import pages.incident._
import pages.locationOfGoods.{AddContactPersonPage, AddressPage, CountryPage, QualifierOfIdentificationPage, TypeOfLocationPage}

class ArrivalDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  private val destinationOffice = arbitrary[CustomsOffice].sample.value
  private val country           = arbitrary[Country].sample.value
  private val address           = arbitrary[DynamicAddress].sample.value
  private val idNumber          = Gen.alphaNumStr.sample.value

  "ArrivalDomain" - {

    "when post transition" - {

      implicit val reader: UserAnswersReader[ArrivalDomain] = ArrivalDomain.userAnswersReader

      "can be parsed from UserAnswers with Incidents" in {

        val initialAnswers = emptyUserAnswers
          .setValue(IncidentFlagPage, true)

        forAll(arbitraryArrivalAnswers(initialAnswers)) {

          userAnswers =>
            val result: EitherType[ArrivalPostTransitionDomain] = UserAnswersReader[ArrivalPostTransitionDomain].run(userAnswers)

            result.value.incidents must not be empty
        }

      }

      "can be parsed from UserAnswers with no Incidents" in {

        val initialAnswers = emptyUserAnswers
          .setValue(IncidentFlagPage, false)

        forAll(arbitraryArrivalAnswers(initialAnswers)) {

          userAnswers =>
            val result: EitherType[ArrivalPostTransitionDomain] = UserAnswersReader[ArrivalPostTransitionDomain].run(userAnswers)

            result.value.incidents must be(empty)
        }

      }

      "cannot be parsed from UserAnswer" - {

        "when a incident flag page is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(DestinationOfficePage, destinationOffice)
            .setValue(IdentificationNumberPage, idNumber)
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
            .setValue(TypeOfLocationPage, AuthorisedPlace)
            .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
            .setValue(CountryPage, country)
            .setValue(AddressPage, address)
            .setValue(AddContactPersonPage, false)

          val result: EitherType[ArrivalDomain] = UserAnswersReader[ArrivalDomain].run(userAnswers)

          result.left.value.page mustBe IncidentFlagPage

        }
      }
    }

    "when pre-transition" ignore {

      implicit val reader: UserAnswersReader[ArrivalDomain] = ArrivalDomain.userAnswersReader

      "can be parsed from UserAnswers" in {

        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IdentificationNumberPage, idNumber)
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(TypeOfLocationPage, AuthorisedPlace)
          .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
          .setValue(CountryPage, country)
          .setValue(AddressPage, address)
          .setValue(AddContactPersonPage, false)

        val expectedResult = ArrivalTransitionDomain(
          IdentificationDomain(
            userAnswers.mrn,
            destinationOffice = destinationOffice,
            identificationNumber = idNumber,
            procedureType = ProcedureType.Normal,
            authorisations = None
          ),
          LocationOfGoodsDomain(
            typeOfLocation = AuthorisedPlace,
            qualifierOfIdentificationDetails = AddressDomain(
              country = country,
              address = address,
              contactPerson = None
            )
          )
        )

        val result: EitherType[ArrivalDomain] = UserAnswersReader[ArrivalDomain].run(userAnswers)

        result.value mustBe expectedResult

      }
    }
  }
}
