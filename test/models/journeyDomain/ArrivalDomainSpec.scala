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
import config.Constants.QualifierCode.*
import generators.Generators
import models.DynamicAddress
import models.identification.ProcedureType
import models.journeyDomain.identification.IdentificationDomain
import models.journeyDomain.locationOfGoods.{AddressDomain, LocationOfGoodsDomain}
import models.reference.*
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.{DestinationOfficePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import pages.locationOfGoods.*

class ArrivalDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks with AppWithDefaultMockFixtures {

  private val destinationOffice = arbitrary[CustomsOffice].sample.value
  private val country           = arbitrary[Country].sample.value
  private val address           = arbitrary[DynamicAddress].sample.value
  private val idNumber          = Gen.alphaNumStr.sample.value
  private val typeOfLocation    = arbitrary[TypeOfLocation].sample.value

  "ArrivalDomain" - {

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

      val expectedResult = ArrivalDomain(
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

      val result = ArrivalDomain.userAnswersReader.apply(Nil).run(userAnswers)

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
}
