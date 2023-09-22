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
import forms.Constants._
import generators.Generators
import models.identification.ProcedureType
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.{Country, CustomsOffice}
import models.{Coordinates, DynamicAddress, PostalCodeAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods._

class QualifierOfIdentificationDomainSpec extends SpecBase with Generators {

  private val customsOffice = arbitrary[CustomsOffice].sample.value
  private val country       = arbitrary[Country].sample.value
  private val address       = arbitrary[DynamicAddress].sample.value
  private val coordinates   = arbitrary[Coordinates].sample.value
  private val idNumber      = Gen.alphaNumStr.sample.value
  private val name          = Gen.alphaNumStr.sample.value
  private val tel           = Gen.alphaNumStr.sample.value
  private val unLocode      = arbitrary[String].sample.value
  private val postalCode    = arbitrary[PostalCodeAddress].sample.value

  "QualifierOfIdentificationDomain" - {

    "can be parsed from UserAnswers from AddressDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(AddressCode).sample.value)
        .setValue(CountryPage, country)
        .setValue(AddressPage, address)
        .setValue(AddContactPersonPage, false)

      val expectedResult = AddressDomain(
        country,
        address,
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from EoriNumberDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(EoriNumberCode).sample.value)
        .setValue(IdentificationNumberPage, idNumber)
        .setValue(AddContactPersonPage, false)

      val expectedResult = EoriNumberDomain(
        idNumber,
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from AuthorisationNumberDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(AuthorisationNumberCode).sample.value)
        .setValue(AuthorisationNumberPage, idNumber)
        .setValue(AddContactPersonPage, false)

      val expectedResult = AuthorisationNumberDomain(
        idNumber,
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from CoordinatesDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(CoordinatesCode).sample.value)
        .setValue(CoordinatesPage, coordinates)
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, name)
        .setValue(ContactPersonTelephonePage, tel)

      val expectedResult = CoordinatesDomain(
        coordinates,
        Some(ContactPersonDomain(name, tel))
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from CustomsOfficeDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(CustomsOfficeCode).sample.value)
        .setValue(CustomsOfficePage, customsOffice)

      val expectedResult = CustomsOfficeDomain(customsOffice)

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers from UnlocodeDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(UnlocodeCode).sample.value)
        .setValue(UnlocodePage, unLocode)
        .setValue(AddContactPersonPage, false)

      val expectedResult = UnlocodeDomain(
        unLocode,
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers from PostalCode" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(PostalCodeCode).sample.value)
        .setValue(PostalCodePage, postalCode)
        .setValue(AddContactPersonPage, false)

      val expectedResult = PostalCodeDomain(
        postalCode,
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val result: EitherType[QualifierOfIdentificationDomain] =
          UserAnswersReader[QualifierOfIdentificationDomain].run(emptyUserAnswers.setValue(IsSimplifiedProcedurePage, ProcedureType.Normal))

        result.left.value.page mustBe QualifierOfIdentificationPage
      }
    }
  }

  "AddressDomain" - {

    "can be parsed from UserAnswers with contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(CountryPage, country)
        .setValue(AddressPage, address)
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, name)
        .setValue(ContactPersonTelephonePage, tel)

      val expectedResult = AddressDomain(
        country,
        address,
        Some(ContactPersonDomain(name, tel))
      )

      val result: EitherType[AddressDomain] = UserAnswersReader[AddressDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(CountryPage, country)
        .setValue(AddressPage, address)
        .setValue(AddContactPersonPage, false)

      val expectedResult = AddressDomain(
        country,
        address,
        None
      )

      val result: EitherType[AddressDomain] = UserAnswersReader[AddressDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(AddressPage, AddContactPersonPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(CountryPage, country)
          .setValue(AddressPage, address)
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        mandatoryPages.map {
          page =>
            val updatedUserAnswers = userAnswers.removeValue(page)

            val result: EitherType[AddressDomain] = UserAnswersReader[AddressDomain].run(updatedUserAnswers)

            result.left.value.page mustBe page
        }
      }
    }
  }

  "IdentificationNumberDomain" - {

    "can be parsed from UserAnswers with contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(IdentificationNumberPage, idNumber)
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, name)
        .setValue(ContactPersonTelephonePage, tel)

      val expectedResult = EoriNumberDomain(
        idNumber,
        Some(ContactPersonDomain(name, tel))
      )

      val result: EitherType[EoriNumberDomain] = UserAnswersReader[EoriNumberDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" - {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(IdentificationNumberPage, idNumber)
        .setValue(AddContactPersonPage, false)

      val expectedResult = EoriNumberDomain(
        idNumber,
        None
      )

      val result: EitherType[EoriNumberDomain] = UserAnswersReader[EoriNumberDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(IdentificationNumberPage, AddContactPersonPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(IdentificationNumberPage, idNumber)
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        mandatoryPages.map {
          page =>
            val updatedUserAnswers = userAnswers.removeValue(page)

            val result: EitherType[EoriNumberDomain] = UserAnswersReader[EoriNumberDomain].run(updatedUserAnswers)

            result.left.value.page mustBe page
        }
      }
    }

  }

  "AuthorisationNumberDomain" - {

    "when procedure type normal" - {
      "can be parsed from UserAnswers with contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(AuthorisationNumberPage, idNumber)
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        val expectedResult = AuthorisationNumberDomain(
          idNumber,
          Some(ContactPersonDomain(name, tel))
        )

        val result: EitherType[AuthorisationNumberDomain] = UserAnswersReader[AuthorisationNumberDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "can be parsed from UserAnswers without contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(AuthorisationNumberPage, idNumber)
          .setValue(AddContactPersonPage, false)

        val expectedResult = AuthorisationNumberDomain(
          idNumber,
          None
        )

        val result: EitherType[AuthorisationNumberDomain] = UserAnswersReader[AuthorisationNumberDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(AuthorisationNumberPage, AddContactPersonPage)

        "when a mandatory page is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
            .setValue(AuthorisationNumberPage, idNumber)
            .setValue(AddContactPersonPage, true)
            .setValue(ContactPersonNamePage, name)
            .setValue(ContactPersonTelephonePage, tel)

          mandatoryPages.map {
            page =>
              val updatedUserAnswers = userAnswers.removeValue(page)

              val result: EitherType[AuthorisationNumberDomain] = UserAnswersReader[AuthorisationNumberDomain].run(updatedUserAnswers)

              result.left.value.page mustBe page
          }
        }
      }

    }

    "CoordinatesDomain" - {

      "can be parsed from UserAnswers with contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(CoordinatesPage, coordinates)
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        val expectedResult = CoordinatesDomain(
          coordinates,
          Some(ContactPersonDomain(name, tel))
        )

        val result: EitherType[CoordinatesDomain] = UserAnswersReader[CoordinatesDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "can be parsed from UserAnswers without contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(CoordinatesPage, coordinates)
          .setValue(AddContactPersonPage, false)

        val expectedResult = CoordinatesDomain(
          coordinates,
          None
        )

        val result: EitherType[CoordinatesDomain] = UserAnswersReader[CoordinatesDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(CoordinatesPage, AddContactPersonPage)

        "when a mandatory page is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
            .setValue(CoordinatesPage, coordinates)
            .setValue(AddContactPersonPage, true)
            .setValue(ContactPersonNamePage, name)
            .setValue(ContactPersonTelephonePage, tel)

          mandatoryPages.map {
            page =>
              val updatedUserAnswers = userAnswers.removeValue(page)

              val result: EitherType[CoordinatesDomain] = UserAnswersReader[CoordinatesDomain].run(updatedUserAnswers)

              result.left.value.page mustBe page
          }
        }
      }
    }

    "CustomsOfficeDomain" - {

      "can be parsed from UserAnswers" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(CustomsOfficePage, customsOffice)

        val expectedResult = CustomsOfficeDomain(customsOffice)

        val result: EitherType[CustomsOfficeDomain] = UserAnswersReader[CustomsOfficeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "cannot be parsed from UserAnswers" - {

        "when a mandatory page is missing" in {

          val result: EitherType[CustomsOfficeDomain] = UserAnswersReader[CustomsOfficeDomain].run(emptyUserAnswers)

          result.left.value.page mustBe CustomsOfficePage
        }
      }
    }

    "UnlocodeDomain" - {

      "can be parsed from UserAnswers with contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(UnlocodePage, unLocode)
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        val expectedResult = UnlocodeDomain(
          unLocode,
          Some(ContactPersonDomain(name, tel))
        )

        val result: EitherType[UnlocodeDomain] = UserAnswersReader[UnlocodeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "can be parsed from UserAnswers without contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(UnlocodePage, unLocode)
          .setValue(AddContactPersonPage, false)

        val expectedResult = UnlocodeDomain(
          unLocode,
          None
        )

        val result: EitherType[UnlocodeDomain] = UserAnswersReader[UnlocodeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(UnlocodePage, AddContactPersonPage)

        "when a mandatory page is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
            .setValue(UnlocodePage, unLocode)
            .setValue(AddContactPersonPage, true)
            .setValue(ContactPersonNamePage, name)
            .setValue(ContactPersonTelephonePage, tel)

          mandatoryPages.map {
            page =>
              val updatedUserAnswers = userAnswers.removeValue(page)

              val result: EitherType[UnlocodeDomain] = UserAnswersReader[UnlocodeDomain].run(updatedUserAnswers)

              result.left.value.page mustBe page
          }
        }
      }
    }

    "PostalCodeDomain" - {

      "can be parsed from UserAnswers with contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(PostalCodePage, postalCode)
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, name)
          .setValue(ContactPersonTelephonePage, tel)

        val expectedResult = PostalCodeDomain(
          postalCode,
          Some(ContactPersonDomain(name, tel))
        )

        val result: EitherType[PostalCodeDomain] = UserAnswersReader[PostalCodeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "can be parsed from UserAnswers without contact person" in {

        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(PostalCodePage, postalCode)
          .setValue(AddContactPersonPage, false)

        val expectedResult = PostalCodeDomain(
          postalCode,
          None
        )

        val result: EitherType[PostalCodeDomain] = UserAnswersReader[PostalCodeDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(PostalCodePage, AddContactPersonPage)

        "when a mandatory page is missing" in {

          val userAnswers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
            .setValue(PostalCodePage, postalCode)
            .setValue(AddContactPersonPage, true)
            .setValue(ContactPersonNamePage, name)
            .setValue(ContactPersonTelephonePage, tel)

          mandatoryPages.map {
            page =>
              val updatedUserAnswers = userAnswers.removeValue(page)

              val result: EitherType[PostalCodeDomain] = UserAnswersReader[PostalCodeDomain].run(updatedUserAnswers)

              result.left.value.page mustBe page
          }
        }
      }
    }
  }
}
