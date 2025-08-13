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

package utils

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.locationOfGoods.routes
import generators.Generators
import models.reference.{Country, CustomsOffice, QualifierOfIdentification, TypeOfLocation}
import models.{Coordinates, DynamicAddress, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.locationOfGoods.*

class LocationOfGoodsAnswersHelperSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  "LocationOfGoodsAnswersHelper" - {

    "locationType" - {
      "must return None" - {
        "when TypeOfLocationPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationType
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when TypeOfLocationPage defined" in {
          forAll(arbitrary[TypeOfLocation], arbitrary[Mode]) {
            (typeOfLocation, mode) =>
              val answers = emptyUserAnswers.setValue(TypeOfLocationPage, typeOfLocation)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.locationType.get

              result.key.value mustEqual "Location type"
              result.value.value mustEqual typeOfLocation.asString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.TypeOfLocationController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "location type for the goods"
              action.id mustEqual "type-of-location"
          }
        }
      }
    }

    "qualifierOfIdentification" - {
      "must return None" - {
        "when QualifierOfIdentificationPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.qualifierOfIdentification
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when QualifierOfIdentificationPage defined" in {
          forAll(arbitrary[QualifierOfIdentification], arbitrary[Mode]) {
            (qualifierOfIdentification, mode) =>
              val answers = emptyUserAnswers.setValue(QualifierOfIdentificationPage, qualifierOfIdentification)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.qualifierOfIdentification.get

              result.key.value mustEqual "Identifier type"
              result.value.value mustEqual qualifierOfIdentification.asString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.QualifierOfIdentificationController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "identifier type for the location of goods"
              action.id mustEqual "qualifier-of-identification"
          }
        }
      }
    }

    "customsOfficeIdentifier" - {
      "must return None" - {
        "when CustomsOfficePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.customsOfficeIdentifier
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when CustomsOfficePage defined" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Mode]) {
            (customsOffice, mode) =>
              val answers = emptyUserAnswers.setValue(CustomsOfficePage, customsOffice)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.customsOfficeIdentifier.get

              result.key.value mustEqual "Customs office identifier"
              result.value.value mustEqual customsOffice.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.CustomsOfficeController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "customs office identifier for the location of goods"
              action.id mustEqual "customs-office"
          }
        }
      }
    }

    "identificationNumber" - {
      "must return None" - {
        "when IdentificationNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.identificationNumber
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when IdentificationNumberPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (identificationNumber, mode) =>
              val answers = emptyUserAnswers.setValue(IdentificationNumberPage, identificationNumber)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.identificationNumber.get

              result.key.value mustEqual "EORI number or Trader Identification Number (TIN)"
              result.value.value mustEqual identificationNumber
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.IdentificationNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "EORI number or Trader Identification Number (TIN) for the location of goods"
              action.id mustEqual "identification-number"
          }
        }
      }
    }

    "authorisationNumber" - {
      "must return None" - {
        "when AuthorisationNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.authorisationNumber
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationNumberPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (authorisationNumber, mode) =>
              val answers = emptyUserAnswers.setValue(AuthorisationNumberPage, authorisationNumber)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.authorisationNumber.get

              result.key.value mustEqual "Authorisation number"
              result.value.value mustEqual authorisationNumber
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.AuthorisationNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "authorisation number for the location of goods"
              action.id mustEqual "authorisation-number"
          }
        }
      }
    }

    "coordinates" - {
      "must return None" - {
        "when CoordinatesPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.coordinates
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when CoordinatesPage defined" in {
          forAll(arbitrary[Coordinates], arbitrary[Mode]) {
            (coordinates, mode) =>
              val answers = emptyUserAnswers.setValue(CoordinatesPage, coordinates)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.coordinates.get

              result.key.value mustEqual "Coordinates"
              result.value.value mustEqual coordinates.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.CoordinatesController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "coordinates for the location of goods"
              action.id mustEqual "coordinates"
          }
        }
      }
    }

    "unLocode" - {
      "must return None" - {
        "when UnlocodePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.unLocode
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when UnlocodePage defined" in {
          forAll(nonEmptyString, arbitrary[Mode]) {
            (unLocode, mode) =>
              val answers = emptyUserAnswers.setValue(UnlocodePage, unLocode)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.unLocode.get

              result.key.value mustEqual "UN/LOCODE"
              result.value.value mustEqual unLocode.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.UnlocodeController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "UN/LOCODE for the location of goods"
              action.id mustEqual "un-locode"
          }
        }
      }
    }

    "country" - {
      "must return None" - {
        "when CountryPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.country
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when CountryPage defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(CountryPage, country)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.country.get

              result.key.value mustEqual "Country"
              result.value.value mustEqual country.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.CountryController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "country for the location of goods"
              action.id mustEqual "country"
          }
        }
      }
    }

    "address" - {
      "must return None" - {
        "when AddressPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.address
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when AddressPage defined" in {
          forAll(arbitrary[DynamicAddress], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(AddressPage, address)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.address.get

              result.key.value mustEqual "Address"
              result.value.value mustEqual address.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.AddressController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "address for the location of goods"
              action.id mustEqual "address"
          }
        }
      }
    }

    "contactYesNo" - {
      "must return None" - {
        "when AddContactPersonPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactYesNo
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when AddContactPersonPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddContactPersonPage, true)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.contactYesNo.get

              result.key.value mustEqual "Do you want to add a contact for the location of goods?"
              result.value.value mustEqual "Yes"
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.AddContactPersonController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "if you want to add a contact for the location of goods"
              action.id mustEqual "add-contact-person"
          }
        }
      }
    }

    "contactName" - {
      "must return None" - {
        "when ContactPersonNamePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactName
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when ContactPersonNamePage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (name, mode) =>
              val answers = emptyUserAnswers.setValue(ContactPersonNamePage, name)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.contactName.get

              result.key.value mustEqual "Contact name"
              result.value.value mustEqual name
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.ContactPersonNameController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "contact name for the location of goods"
              action.id mustEqual "contact-person-name"
          }
        }
      }
    }

    "contactPhoneNumber" - {
      "must return None" - {
        "when ContactPersonTelephonePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.contactPhoneNumber
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when ContactPersonTelephonePage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (phoneNumber, mode) =>
              val answers = emptyUserAnswers.setValue(ContactPersonTelephonePage, phoneNumber)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.contactPhoneNumber.get

              result.key.value mustEqual "Contact phone number"
              result.value.value mustEqual phoneNumber
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.ContactPersonTelephoneController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "contact phone number for the location of goods"
              action.id mustEqual "contact-person-phone"
          }
        }
      }
    }
  }
}
