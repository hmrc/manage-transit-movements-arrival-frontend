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

import base.SpecBase
import controllers.locationOfGoods.routes
import generators.Generators
import models.reference.{Country, CustomsOffice, QualifierOfIdentification, TypeOfLocation}
import models.{Coordinates, DynamicAddress, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.locationOfGoods.*

class LocationOfGoodsAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "LocationOfGoodsAnswersHelper" - {

    "locationType" - {
      "must return None" - {
        "when TypeOfLocationPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = LocationOfGoodsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.locationType
              result mustBe None
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

              result.key.value mustBe "Location type"
              result.value.value mustBe typeOfLocation.asString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.TypeOfLocationController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "location type for the goods"
              action.id mustBe "type-of-location"
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
              result mustBe None
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

              result.key.value mustBe "Identifier type"
              result.value.value mustBe qualifierOfIdentification.asString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.QualifierOfIdentificationController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "identifier type for the location of goods"
              action.id mustBe "qualifier-of-identification"
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
              result mustBe None
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

              result.key.value mustBe "Customs office identifier"
              result.value.value mustBe customsOffice.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.CustomsOfficeController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "customs office identifier for the location of goods"
              action.id mustBe "customs-office"
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
              result mustBe None
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

              result.key.value mustBe "EORI number or Trader Identification Number (TIN)"
              result.value.value mustBe identificationNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IdentificationNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "EORI number or Trader Identification Number (TIN) for the location of goods"
              action.id mustBe "identification-number"
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
              result mustBe None
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

              result.key.value mustBe "Authorisation number"
              result.value.value mustBe authorisationNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AuthorisationNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "authorisation number for the location of goods"
              action.id mustBe "authorisation-number"
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
              result mustBe None
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

              result.key.value mustBe "Coordinates"
              result.value.value mustBe coordinates.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.CoordinatesController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "coordinates for the location of goods"
              action.id mustBe "coordinates"
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
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when UnlocodePage defined" in {
          forAll(arbitrary[String], arbitrary[Mode]) {
            (unLocode, mode) =>
              val answers = emptyUserAnswers.setValue(UnlocodePage, unLocode)

              val helper = LocationOfGoodsAnswersHelper(answers, mode)
              val result = helper.unLocode.get

              result.key.value mustBe "UN/LOCODE"
              result.value.value mustBe unLocode.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.UnlocodeController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "UN/LOCODE for the location of goods"
              action.id mustBe "un-locode"
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
              result mustBe None
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

              result.key.value mustBe "Country"
              result.value.value mustBe country.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.CountryController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "country for the location of goods"
              action.id mustBe "country"
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
              result mustBe None
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

              result.key.value mustBe "Address"
              result.value.value mustBe address.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AddressController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "address for the location of goods"
              action.id mustBe "address"
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
              result mustBe None
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

              result.key.value mustBe "Do you want to add a contact for the location of goods?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AddContactPersonController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "if you want to add a contact for the location of goods"
              action.id mustBe "add-contact-person"
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
              result mustBe None
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

              result.key.value mustBe "Contact name"
              result.value.value mustBe name
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.ContactPersonNameController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "contact name for the location of goods"
              action.id mustBe "contact-person-name"
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
              result mustBe None
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

              result.key.value mustBe "Contact phone number"
              result.value.value mustBe phoneNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.ContactPersonTelephoneController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "contact phone number for the location of goods"
              action.id mustBe "contact-person-phone"
          }
        }
      }
    }
  }
}
