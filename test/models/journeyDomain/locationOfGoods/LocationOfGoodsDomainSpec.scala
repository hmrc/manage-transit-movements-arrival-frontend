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

package models.journeyDomain.locationOfGoods

import base.SpecBase
import config.Constants.QualifierCode._
import generators.Generators
import models.DynamicAddress
import models.identification.ProcedureType
import models.reference.{Country, TypeOfLocation}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods._

class LocationOfGoodsDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  private val country = arbitrary[Country].sample.value
  private val address = arbitrary[DynamicAddress].sample.value

  "LocationOfGoodsDomain" - {

    "can be parsed from UserAnswers when procedure type is normal" in {

      forAll(arbitrary[TypeOfLocation]) {
        typeOfLocation =>
          val userAnswers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
            .setValue(TypeOfLocationPage, typeOfLocation)
            .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(AddressCode).sample.value)
            .setValue(CountryPage, country)
            .setValue(AddressPage, address)
            .setValue(AddContactPersonPage, false)

          val expectedResult =
            LocationOfGoodsDomain(
              typeOfLocation = Some(typeOfLocation),
              qualifierOfIdentificationDetails = AddressDomain(
                country,
                address,
                None
              )
            )

          val result = LocationOfGoodsDomain.userAnswersReader.apply(Nil).run(userAnswers)

          result.value.value mustBe expectedResult
      }
    }

    "can be parsed from UserAnswers when procedure type is simplified" in {

      val authorisationReference = Gen.alphaNumStr.sample.value

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
        .setValue(AuthorisationNumberPage, authorisationReference)
        .setValue(AddContactPersonPage, false)

      val expectedResult =
        LocationOfGoodsDomain(
          typeOfLocation = None,
          qualifierOfIdentificationDetails = AuthorisationNumberDomain(
            authorisationReference,
            None
          )
        )

      val result = LocationOfGoodsDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing for procedure type normal" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(TypeOfLocationPage, QualifierOfIdentificationPage)

        val typeOfLocation = arbitrary[TypeOfLocation].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(TypeOfLocationPage, typeOfLocation)
          .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(AddressCode).sample.value)
          .setValue(CountryPage, country)
          .setValue(AddressPage, address)
          .setValue(AddContactPersonPage, false)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedUserAnswers = userAnswers.removeValue(mandatoryPage)

            val result = LocationOfGoodsDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }

      "when a mandatory page is missing for procedure type simplified" in {

        val authorisationReference = Gen.alphaNumStr.sample.value

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(AuthorisationNumberPage)

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
          .setValue(AuthorisationNumberPage, authorisationReference)
          .setValue(AddContactPersonPage, false)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedUserAnswers = userAnswers.removeValue(mandatoryPage)

            val result = LocationOfGoodsDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
