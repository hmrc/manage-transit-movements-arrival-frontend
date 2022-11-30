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

package models.journeyDomain.locationOfGoods

import base.SpecBase
import generators.Generators
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.locationOfGoods.TypeOfLocation
import models.{DynamicAddress, QualifierOfIdentification}
import org.scalacheck.Gen
import pages.QuestionPage
import pages.locationOfGoods.{AddContactPersonPage, AddressPage, QualifierOfIdentificationPage, TypeOfLocationPage}

class LocationOfGoodsDomainSpec extends SpecBase with Generators {

  "LocationOfGoodsDomain" - {

    "can be parsed from UserAnswers" in {

      TypeOfLocation.values.map {
        value =>
          val userAnswers = emptyUserAnswers
            .setValue(TypeOfLocationPage, value)
            .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
            .setValue(AddressPage, DynamicAddress("line1", "line2", Some("postalCode")))
            .setValue(AddContactPersonPage, false)

          val expectedResult =
            LocationOfGoodsDomain(
              typeOfLocation = value,
              qualifierOfIdentificationDetails = AddressDomain(
                DynamicAddress("line1", "line2", Some("postalCode")),
                None
              )
            )

          val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(userAnswers)

          result.value mustBe expectedResult
      }
    }

    "cannot be parsed from UserAnswers" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(TypeOfLocationPage, QualifierOfIdentificationPage)

        val typeOfLocation = Gen.oneOf(TypeOfLocation.values).sample.value

        val userAnswers = emptyUserAnswers
          .setValue(TypeOfLocationPage, typeOfLocation)
          .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
          .setValue(AddressPage, DynamicAddress("line1", "line2", Some("postalCode")))
          .setValue(AddContactPersonPage, false)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedUserAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[LocationOfGoodsDomain] = UserAnswersReader[LocationOfGoodsDomain].run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
