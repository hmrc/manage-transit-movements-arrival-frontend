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
import models.{InternationalAddress, UkAddress}
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.locationOfGoods.QualifierOfIdentification
import models.reference.{Country, CountryCode, CustomsOffice}
import pages.LocationOfGoods._
import pages.QuestionPage

class QualifierOfIdentificationDomainSpec extends SpecBase with Generators {

  "QualifierOfIdentificationDomain" - {

    "can be parsed from UserAnswers from AddressDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Address)
        .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
        .setValue(AddContactPersonPage, false)

      val expectedResult = AddressDomain(
        InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from EoriNumberDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.EoriNumber)
        .setValue(IdentificationNumberPage, "identificationNumber")
        .setValue(AddContactPersonPage, false)
        .setValue(AdditionalIdentifierPage, "additionalIdentifier")

      val expectedResult = EoriNumberDomain(
        "identificationNumber",
        None,
        "additionalIdentifier"
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from AuthorisationNumberDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.AuthorisationNumber)
        .setValue(AuthorisationNumberPage, "authorisationNumber")
        .setValue(AddContactPersonPage, false)
        .setValue(AdditionalIdentifierPage, "additionalIdentifier")

      val expectedResult = AuthorisationNumberDomain(
        "authorisationNumber",
        None,
        "additionalIdentifier"
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from CoordinatesDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Coordinates)
        .setValue(CoordinatesPage, "coordinates")
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")

      val expectedResult = CoordinatesDomain(
        "coordinates",
        Some(ContactPerson("contact name", "contact telephone"))
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers from CustomsOfficeDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.CustomsOffice)
        .setValue(CustomsOfficePage, CustomsOffice("GB0001", None, None))

      val expectedResult = CustomsOfficeDomain(CustomsOffice("GB0001", None, None))

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers from UnlocodeDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.Unlocode)
        .setValue(UnlocodePage, "unlocode")
        .setValue(AddContactPersonPage, false)

      val expectedResult = UnlocodeDomain(
        "unlocode",
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers from PostalCode" in {

      val userAnswers = emptyUserAnswers
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.PostalCode)
        .setValue(AddressPage, UkAddress("line1", "line2", "postalCode"))
        .setValue(AddContactPersonPage, false)

      val expectedResult = PostalCodeDomain(
        UkAddress("line1", "line2", "postalCode"),
        None
      )

      val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val result: EitherType[QualifierOfIdentificationDomain] = UserAnswersReader[QualifierOfIdentificationDomain].run(emptyUserAnswers)

        result.left.value.page mustBe QualifierOfIdentificationPage
      }
    }
  }

  "AddressDomain" - {

    "can be parsed from UserAnswers with contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")

      val expectedResult = AddressDomain(
        InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
        Some(ContactPerson("contact name", "contact telephone"))
      )

      val result: EitherType[AddressDomain] = UserAnswersReader[AddressDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
        .setValue(AddContactPersonPage, false)

      val expectedResult = AddressDomain(
        InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")),
        None
      )

      val result: EitherType[AddressDomain] = UserAnswersReader[AddressDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(InternationalAddressPage, AddContactPersonPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(InternationalAddressPage, InternationalAddress("line1", "line2", "postalCode", Country(CountryCode("GB"), "description")))
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, "contact name")
          .setValue(ContactPersonTelephonePage, "contact telephone")

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
        .setValue(IdentificationNumberPage, "identificationNumber")
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")
        .setValue(AdditionalIdentifierPage, "additionalIdentifier")

      val expectedResult = EoriNumberDomain(
        "identificationNumber",
        Some(ContactPerson("contact name", "contact telephone")),
        "additionalIdentifier"
      )

      val result: EitherType[EoriNumberDomain] = UserAnswersReader[EoriNumberDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" - {

      val userAnswers = emptyUserAnswers
        .setValue(IdentificationNumberPage, "identificationNumber")
        .setValue(AddContactPersonPage, false)
        .setValue(AdditionalIdentifierPage, "additionalIdentifier")

      val expectedResult = EoriNumberDomain(
        "identificationNumber",
        None,
        "additionalIdentifier"
      )

      val result: EitherType[EoriNumberDomain] = UserAnswersReader[EoriNumberDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(IdentificationNumberPage, AddContactPersonPage, AdditionalIdentifierPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(IdentificationNumberPage, "identificationNumber")
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, "contact name")
          .setValue(ContactPersonTelephonePage, "contact telephone")
          .setValue(AdditionalIdentifierPage, "additionalIdentifier")

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

    "can be parsed from UserAnswers with contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationNumberPage, "authorisationNumber")
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")
        .setValue(AdditionalIdentifierPage, "additionalIdentifier")

      val expectedResult = AuthorisationNumberDomain(
        "authorisationNumber",
        Some(ContactPerson("contact name", "contact telephone")),
        "additionalIdentifier"
      )

      val result: EitherType[AuthorisationNumberDomain] = UserAnswersReader[AuthorisationNumberDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationNumberPage, "authorisationNumber")
        .setValue(AddContactPersonPage, false)
        .setValue(AdditionalIdentifierPage, "additionalIdentifier")

      val expectedResult = AuthorisationNumberDomain(
        "authorisationNumber",
        None,
        "additionalIdentifier"
      )

      val result: EitherType[AuthorisationNumberDomain] = UserAnswersReader[AuthorisationNumberDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(AuthorisationNumberPage, AddContactPersonPage, AdditionalIdentifierPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(AuthorisationNumberPage, "authorisationNumber")
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, "contact name")
          .setValue(ContactPersonTelephonePage, "contact telephone")
          .setValue(AdditionalIdentifierPage, "additionalIdentifier")

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
        .setValue(CoordinatesPage, "coordinates")
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")

      val expectedResult = CoordinatesDomain(
        "coordinates",
        Some(ContactPerson("contact name", "contact telephone"))
      )

      val result: EitherType[CoordinatesDomain] = UserAnswersReader[CoordinatesDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(CoordinatesPage, "coordinates")
        .setValue(AddContactPersonPage, false)

      val expectedResult = CoordinatesDomain(
        "coordinates",
        None
      )

      val result: EitherType[CoordinatesDomain] = UserAnswersReader[CoordinatesDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(CoordinatesPage, AddContactPersonPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(CoordinatesPage, "coordinates")
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, "contact name")
          .setValue(ContactPersonTelephonePage, "contact telephone")

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
        .setValue(CustomsOfficePage, CustomsOffice("GB0001", None, None))

      val expectedResult = CustomsOfficeDomain(CustomsOffice("GB0001", None, None))

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
        .setValue(UnlocodePage, "unlocode")
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")

      val expectedResult = UnlocodeDomain(
        "unlocode",
        Some(ContactPerson("contact name", "contact telephone"))
      )

      val result: EitherType[UnlocodeDomain] = UserAnswersReader[UnlocodeDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(UnlocodePage, "unlocode")
        .setValue(AddContactPersonPage, false)

      val expectedResult = UnlocodeDomain(
        "unlocode",
        None
      )

      val result: EitherType[UnlocodeDomain] = UserAnswersReader[UnlocodeDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(UnlocodePage, AddContactPersonPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(UnlocodePage, "unlocode")
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, "contact name")
          .setValue(ContactPersonTelephonePage, "contact telephone")

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
        .setValue(AddressPage, UkAddress("line1", "line2", "postalCode"))
        .setValue(AddContactPersonPage, true)
        .setValue(ContactPersonNamePage, "contact name")
        .setValue(ContactPersonTelephonePage, "contact telephone")

      val expectedResult = PostalCodeDomain(
        UkAddress("line1", "line2", "postalCode"),
        Some(ContactPerson("contact name", "contact telephone"))
      )

      val result: EitherType[PostalCodeDomain] = UserAnswersReader[PostalCodeDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "can be parsed from UserAnswers without contact person" in {

      val userAnswers = emptyUserAnswers
        .setValue(AddressPage, UkAddress("line1", "line2", "postalCode"))
        .setValue(AddContactPersonPage, false)

      val expectedResult = PostalCodeDomain(
        UkAddress("line1", "line2", "postalCode"),
        None
      )

      val result: EitherType[PostalCodeDomain] = UserAnswersReader[PostalCodeDomain].run(userAnswers)

      result.value mustBe expectedResult
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[_]] = Seq(AddressPage, AddContactPersonPage)

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(AddressPage, UkAddress("line1", "line2", "postalCode"))
          .setValue(AddContactPersonPage, true)
          .setValue(ContactPersonNamePage, "contact name")
          .setValue(ContactPersonTelephonePage, "contact telephone")

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
