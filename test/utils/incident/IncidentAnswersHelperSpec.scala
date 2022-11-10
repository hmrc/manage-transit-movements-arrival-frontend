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

package utils.incident

import base.SpecBase
import controllers.incident.location.{routes => locationRoutes}
import controllers.incident.routes
import generators.Generators
import models.incident.IncidentCode
import models.reference.{Country, UnLocode}
import models.{Coordinates, DynamicAddress, Mode, QualifierOfIdentification}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident._
import pages.incident.location.{AddressPage, CoordinatesPage, QualifierOfIdentificationPage, UnLocodePage}

import java.time.LocalDate

class IncidentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "CheckIncidentAnswersHelper" - {

    "country" - {
      "must return None" - {
        "when IncidentCountryPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.country
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when IncidentCountryPage defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(IncidentCountryPage(incidentIndex), country)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.country.get

              result.key.value mustBe "Country"
              result.value.value mustBe country.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IncidentCountryController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "incident country"
              action.id mustBe "change-country"
          }
        }
      }
    }

    "code" - {
      "must return None" - {
        "when IncidentCodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.code
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when IncidentCodePage defined" in {
          forAll(arbitrary[IncidentCode], arbitrary[Mode]) {
            (code, mode) =>
              val answers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), code)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.code.get

              result.key.value mustBe "Incident code"
              val key = s"incident.incidentCode.$code"
              messages.isDefinedAt(key) mustBe true
              result.value.value mustBe messages(key)
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IncidentCodeController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "incident code"
              action.id mustBe "change-code"
          }
        }
      }
    }

    "text" - {
      "must return None" - {
        "when IncidentTextPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.text
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when IncidentTextPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (text, mode) =>
              val answers = emptyUserAnswers.setValue(IncidentTextPage(incidentIndex), text)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.text.get

              result.key.value mustBe "Description"
              result.value.value mustBe text
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IncidentTextController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "incident description"
              action.id mustBe "change-text"
          }
        }
      }
    }

    "endorsement-yes-no" - {
      "must return None" - {
        "when AddEndorsementPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.endorsementYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddEndorsementPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddEndorsementPage(incidentIndex), true)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementYesNo.get

              result.key.value mustBe "Do you need to add an endorsement for the incident?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AddEndorsementController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "if you need to add an endorsement for the incident"
              action.id mustBe "change-add-endorsement"
          }
        }
      }
    }

    "endorsement-date" - {
      "must return None" - {
        "when EndorsementDatePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.endorsementDate
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when EndorsementDatePage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val date    = LocalDate.of(2021, 9, 9)
              val answers = emptyUserAnswers.setValue(EndorsementDatePage(incidentIndex), date)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementDate.get

              result.key.value mustBe "Endorsement date"
              result.value.value mustBe "9 September 2021"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.EndorsementDateController.onPageLoad(answers.mrn, incidentIndex, mode).url
              action.visuallyHiddenText.get mustBe "endorsement date"
              action.id mustBe "change-endorsement-date"
          }
        }
      }
    }

    "endorsement-authority" - {
      "must return None" - {
        "when EndorsementAuthorityPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.endorsementAuthority
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when EndorsementDatePage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (authority, mode) =>
              val answers = emptyUserAnswers.setValue(EndorsementAuthorityPage(incidentIndex), authority)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementAuthority.get

              result.key.value mustBe "Authority"
              result.value.value mustBe authority
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.EndorsementAuthorityController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "endorsement authority"
              action.id mustBe "change-endorsement-authority"
          }
        }
      }
    }

    "endorsement-country" - {
      "must return None" - {
        "when EndorsementCountryPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.endorsementCountry
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when EndorsementCountryPage defined" in {
          forAll(arbitrary[Country], arbitrary[Mode]) {
            (country, mode) =>
              val answers = emptyUserAnswers.setValue(EndorsementCountryPage(incidentIndex), country)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementCountry.get

              result.key.value mustBe "Country"
              result.value.value mustBe country.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.EndorsementCountryController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "endorsement country"
              action.id mustBe "change-endorsement-country"
          }
        }
      }
    }

    "endorsement-location" - {
      "must return None" - {
        "when EndorsementLocationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.endorsementLocation
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when EndorsementLocationPage defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (location, mode) =>
              val answers = emptyUserAnswers.setValue(EndorsementLocationPage(incidentIndex), location)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementLocation.get

              result.key.value mustBe "Location"
              result.value.value mustBe location
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.EndorsementLocationController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "endorsement location"
              action.id mustBe "change-endorsement-location"
          }
        }
      }
    }

    "QualifierOfIdentificationPage" - {
      "must return None" - {
        "when QualifierOfIdentificationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.qualifierOfIdentification
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when QualifierOfIdentificationPage defined" in {
          forAll(arbitrary[QualifierOfIdentification], arbitrary[Mode]) {
            (identificationType, mode) =>
              val answers = emptyUserAnswers.setValue(QualifierOfIdentificationPage(incidentIndex), identificationType)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.qualifierOfIdentification.get

              result.key.value mustBe "Identifier type"
              val key = s"qualifierOfIdentification.$identificationType"
              messages.isDefinedAt(key) mustBe true
              result.value.value mustBe messages(key)
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe locationRoutes.QualifierOfIdentificationController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "identifier type for the incident"
              action.id mustBe "change-qualifier-of-identification"
          }
        }
      }
    }

    "UnLocodePage" - {
      "must return None" - {
        "when UnLocodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.unLocode
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when UnLocodePage is defined" in {
          forAll(arbitrary[UnLocode], arbitrary[Mode]) {
            (unlocode, mode) =>
              val answers = emptyUserAnswers.setValue(UnLocodePage(incidentIndex), unlocode)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.unLocode.get

              result.key.value mustBe "UN/LOCODE"
              result.value.value mustBe unlocode.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe locationRoutes.UnLocodeController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "UN/LOCODE for the incident"
              action.id mustBe "change-unlocode"
          }
        }
      }
    }
    "CoordinatesPage" - {
      "must return None" - {
        "when CoordinatesPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.coordinates
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when CoordinatesPage is defined" in {
          forAll(arbitrary[Coordinates], arbitrary[Mode]) {
            (coordinates, mode) =>
              val answers = emptyUserAnswers.setValue(CoordinatesPage(incidentIndex), coordinates)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.coordinates.get

              result.key.value mustBe "Coordinates"
              result.value.value mustBe coordinates.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe locationRoutes.CoordinatesController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "coordinates for the incident"
              action.id mustBe "change-coordinates"
          }
        }
      }
    }
    "AddressPage" - {
      "must return None" - {
        "when AddressPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.address
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when AddressPage is defined" in {
          forAll(arbitrary[DynamicAddress], arbitrary[Mode]) {
            (address, mode) =>
              val answers = emptyUserAnswers.setValue(AddressPage(incidentIndex), address)

              val helper = new IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.address.get

              result.key.value mustBe "Address"
              result.value.value mustBe address.toString //TODO: Fix this not working ???
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe locationRoutes.AddressController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "address for the incident"
              action.id mustBe "change-address"
          }
        }
      }
    }
  }
}
