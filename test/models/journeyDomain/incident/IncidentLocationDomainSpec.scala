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

package models.journeyDomain.incident

import base.SpecBase
import generators.Generators
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.locationOfGoods.QualifierOfIdentification
import models.reference.UnLocode
import models.{Coordinates, DynamicAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.location._

class IncidentLocationDomainSpec extends SpecBase with Generators with ScalaCheckPropertyChecks {

  private val address      = arbitrary[DynamicAddress].sample.value
  private val unLocode     = arbitrary[UnLocode].sample.value
  private val coordinates  = arbitrary[Coordinates].sample.value

  "IncidentLocationDomain" - {

    "can be parsed from UserAnswers " - {

      "when qualifierOfIdentification is coordinates" in {
        val userAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage(index), QualifierOfIdentification.Coordinates)
          .setValue(CoordinatesPage(index), coordinates)

        val expectedResult = IncidentCoordinatesLocationDomain(
          coordinates = coordinates
        )

        val result: EitherType[IncidentLocationDomain] =
          UserAnswersReader[IncidentLocationDomain](IncidentLocationDomain.userAnswersReader(index)).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when qualifierOfIdentification is UnLocode" in {
        val userAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage(index), QualifierOfIdentification.Unlocode)
          .setValue(UnLocodePage(index), unLocode)

        val expectedResult = IncidentUnLocodeLocationDomain(
          unLocode = unLocode
        )

        val result: EitherType[IncidentLocationDomain] =
          UserAnswersReader[IncidentLocationDomain](IncidentLocationDomain.userAnswersReader(index)).run(userAnswers)

        result.value mustBe expectedResult
      }

      "when qualifierOfIdentification is Address" in {
        val userAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage(index), QualifierOfIdentification.Address)
          .setValue(AddressPage(index), address)

        val expectedResult = IncidentAddressLocationDomain(
          address = address
        )

        val result: EitherType[IncidentLocationDomain] =
          UserAnswersReader[IncidentLocationDomain](IncidentLocationDomain.userAnswersReader(index)).run(userAnswers)

        result.value mustBe expectedResult
      }

    }

    "cannot be parsed from UserAnswer" - {
      "when qualifierOfIdentification question is unanswered" in {

        val result: EitherType[IncidentLocationDomain] =
          UserAnswersReader[IncidentLocationDomain](IncidentLocationDomain.userAnswersReader(index)).run(emptyUserAnswers)

        result.left.value.page mustBe QualifierOfIdentificationPage(index)
      }

      "when qualifierOfIdentification is not one of the location values" in {
        forAll(arbitrary[QualifierOfIdentification](arbitraryNonLocationQualifierOfIdentification)) {
          qualifier =>
            val userAnswers = emptyUserAnswers
              .setValue(QualifierOfIdentificationPage(index), qualifier)

            val result: EitherType[IncidentLocationDomain] =
              UserAnswersReader[IncidentLocationDomain](IncidentLocationDomain.userAnswersReader(index)).run(userAnswers)

            result.left.value.page mustBe QualifierOfIdentificationPage(index)
        }
      }
    }
  }

}
