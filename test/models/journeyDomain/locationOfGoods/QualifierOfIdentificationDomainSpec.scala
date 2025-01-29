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
import config.Constants.QualifierCode.*
import generators.Generators
import models.identification.ProcedureType
import models.reference.{Country, CustomsOffice}
import models.{Coordinates, DynamicAddress}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.*

class QualifierOfIdentificationDomainSpec extends SpecBase with Generators {

  private val customsOffice = arbitrary[CustomsOffice].sample.value
  private val country       = arbitrary[Country].sample.value
  private val address       = arbitrary[DynamicAddress].sample.value
  private val coordinates   = arbitrary[Coordinates].sample.value
  private val idNumber      = Gen.alphaNumStr.sample.value
  private val name          = Gen.alphaNumStr.sample.value
  private val tel           = Gen.alphaNumStr.sample.value
  private val unLocode      = arbitrary[String].sample.value

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

      val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        QualifierOfIdentificationPage,
        CountryPage,
        AddressPage,
        AddContactPersonPage
      )
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

      val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        QualifierOfIdentificationPage,
        IdentificationNumberPage,
        AddContactPersonPage
      )
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

      val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        QualifierOfIdentificationPage,
        AuthorisationNumberPage,
        AddContactPersonPage
      )
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

      val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        QualifierOfIdentificationPage,
        CoordinatesPage,
        AddContactPersonPage,
        ContactPersonNamePage,
        ContactPersonTelephonePage
      )
    }

    "can be parsed from UserAnswers from CustomsOfficeDomain" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
        .setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(CustomsOfficeCode).sample.value)
        .setValue(CustomsOfficePage, customsOffice)

      val expectedResult = CustomsOfficeDomain(customsOffice)

      val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        QualifierOfIdentificationPage,
        CustomsOfficePage
      )
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

      val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        QualifierOfIdentificationPage,
        UnlocodePage,
        AddContactPersonPage
      )
    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers.setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)

        val result = QualifierOfIdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustBe QualifierOfIdentificationPage
        result.left.value.pages mustBe Seq(
          QualifierOfIdentificationPage
        )
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

      val result = AddressDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        CountryPage,
        AddressPage,
        AddContactPersonPage,
        ContactPersonNamePage,
        ContactPersonTelephonePage
      )
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

      val result = AddressDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        CountryPage,
        AddressPage,
        AddContactPersonPage
      )
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[?]] = Seq(AddressPage, AddContactPersonPage)

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

            val result = AddressDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

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

      val result = EoriNumberDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        IdentificationNumberPage,
        AddContactPersonPage,
        ContactPersonNamePage,
        ContactPersonTelephonePage
      )
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

      val result = EoriNumberDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        IdentificationNumberPage,
        AddContactPersonPage
      )
    }

    "cannot be parsed from UserAnswers" - {

      val mandatoryPages: Seq[QuestionPage[?]] = Seq(IdentificationNumberPage, AddContactPersonPage)

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

            val result = EoriNumberDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

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

        val result = AuthorisationNumberDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          AuthorisationNumberPage,
          AddContactPersonPage,
          ContactPersonNamePage,
          ContactPersonTelephonePage
        )
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

        val result = AuthorisationNumberDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          AuthorisationNumberPage,
          AddContactPersonPage
        )
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[?]] = Seq(AuthorisationNumberPage, AddContactPersonPage)

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

              val result = AuthorisationNumberDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

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

        val result = CoordinatesDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          CoordinatesPage,
          AddContactPersonPage,
          ContactPersonNamePage,
          ContactPersonTelephonePage
        )
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

        val result = CoordinatesDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          CoordinatesPage,
          AddContactPersonPage
        )
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[?]] = Seq(CoordinatesPage, AddContactPersonPage)

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

              val result = CoordinatesDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

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

        val result = CustomsOfficeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          CustomsOfficePage
        )
      }

      "cannot be parsed from UserAnswers" - {

        "when a mandatory page is missing" in {

          val result = CustomsOfficeDomain.userAnswersReader.apply(Nil).run(emptyUserAnswers)

          result.left.value.page mustBe CustomsOfficePage
          result.left.value.pages mustBe Seq(
            CustomsOfficePage
          )
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

        val result = UnlocodeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          UnlocodePage,
          AddContactPersonPage,
          ContactPersonNamePage,
          ContactPersonTelephonePage
        )
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

        val result = UnlocodeDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          UnlocodePage,
          AddContactPersonPage
        )
      }

      "cannot be parsed from UserAnswers" - {

        val mandatoryPages: Seq[QuestionPage[?]] = Seq(UnlocodePage, AddContactPersonPage)

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

              val result = UnlocodeDomain.userAnswersReader.apply(Nil).run(updatedUserAnswers)

              result.left.value.page mustBe page
          }
        }
      }
    }
  }
}
