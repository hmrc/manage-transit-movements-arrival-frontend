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

package pages.incident.location

import config.Constants.QualifierCode._
import models.reference.QualifierOfIdentification
import models.{Coordinates, DynamicAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class QualifierOfIdentificationPageSpec extends PageBehaviours {

  "QualifierOfIdentificationPage" - {

    beRetrievable[QualifierOfIdentification](QualifierOfIdentificationPage(index))

    beSettable[QualifierOfIdentification](QualifierOfIdentificationPage(index))

    beRemovable[QualifierOfIdentification](QualifierOfIdentificationPage(index))
  }

  "cleanup" - {
    "when Coordinates is selected" - {
      "must remove Unlocode page" in {
        forAll(arbitrary[String]) {
          unlocode =>
            val userAnswers = emptyUserAnswers
              .setValue(UnLocodePage(index), unlocode)

            val result = userAnswers.setValue(QualifierOfIdentificationPage(index), qualifierOfIdentificationGen(CoordinatesCode).sample.value)

            result.get(UnLocodePage(index)) mustNot be(defined)
        }
      }

      "must remove Address page" in {
        forAll(arbitrary[DynamicAddress]) {
          address =>
            val userAnswers = emptyUserAnswers
              .setValue(AddressPage(index), address)

            val result = userAnswers.setValue(QualifierOfIdentificationPage(index), qualifierOfIdentificationGen(CoordinatesCode).sample.value)

            result.get(AddressPage(index)) mustNot be(defined)
        }
      }
    }
    "when UnLocode is selected" - {
      "must remove Coordinates page" in {
        forAll(arbitrary[Coordinates]) {
          coordinates =>
            val userAnswers = emptyUserAnswers
              .setValue(CoordinatesPage(index), coordinates)

            val result = userAnswers.setValue(QualifierOfIdentificationPage(index), qualifierOfIdentificationGen(AddressCode).sample.value)

            result.get(CoordinatesPage(index)) mustNot be(defined)
        }
      }

      "must remove Address page" in {
        forAll(arbitrary[DynamicAddress]) {
          address =>
            val userAnswers = emptyUserAnswers
              .setValue(AddressPage(index), address)

            val result = userAnswers.setValue(QualifierOfIdentificationPage(index), qualifierOfIdentificationGen(UnlocodeCode).sample.value)

            result.get(AddressPage(index)) mustNot be(defined)
        }
      }
    }
    "when Address is selected" - {
      "must remove Coordinates page" in {
        forAll(arbitrary[Coordinates]) {
          coordinates =>
            val userAnswers = emptyUserAnswers
              .setValue(CoordinatesPage(index), coordinates)

            val result = userAnswers.setValue(QualifierOfIdentificationPage(index), qualifierOfIdentificationGen(AddressCode).sample.value)

            result.get(CoordinatesPage(index)) mustNot be(defined)
        }
      }

      "must remove Unlocode page" in {
        forAll(arbitrary[String]) {
          unLocode =>
            val userAnswers = emptyUserAnswers
              .setValue(UnLocodePage(index), unLocode)

            val result = userAnswers.setValue(QualifierOfIdentificationPage(index), qualifierOfIdentificationGen(AddressCode).sample.value)

            result.get(UnLocodePage(index)) mustNot be(defined)
        }
      }
    }

  }

}
