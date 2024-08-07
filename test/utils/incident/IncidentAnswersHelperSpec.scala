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

package utils.incident

import base.SpecBase
import config.Constants.IncidentCode._
import controllers.incident.endorsement.{routes => endorsementRoutes}
import controllers.incident.equipment.{routes => equipmentRoutes}
import controllers.incident.location.{routes => locationRoutes}
import controllers.incident.routes
import controllers.incident.transportMeans.{routes => transportMeansRoutes}
import generators.Generators
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.reference._
import models.{Coordinates, DynamicAddress, Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident._
import pages.incident.endorsement.{EndorsementAuthorityPage, EndorsementCountryPage, EndorsementDatePage, EndorsementLocationPage}
import pages.incident.equipment.ContainerIdentificationNumberYesNoPage
import pages.incident.location.{AddressPage, CoordinatesPage, QualifierOfIdentificationPage, UnLocodePage}
import pages.incident.transportMeans.{IdentificationNumberPage, IdentificationPage, TransportNationalityPage}
import pages.sections.incident.EquipmentSection
import play.api.libs.json.Json

import java.time.LocalDate

class IncidentAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "IncidentAnswersHelper" - {

    "equipments" - {
      "must return no rows" - {
        "when no equipments defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.equipments
              result mustBe Nil
          }
        }
      }

      "must return rows" - {
        "when equipments defined" in {
          forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxTransportEquipments)) {
            (mode, count) =>
              val userAnswersGen = (0 until count).foldLeft(Gen.const(emptyUserAnswers)) {
                (acc, i) =>
                  acc.flatMap(arbitraryEquipmentAnswers(_, incidentIndex, Index(i)))
              }
              forAll(userAnswersGen) {
                userAnswers =>
                  val helper = IncidentAnswersHelper(userAnswers, mode, incidentIndex)
                  val result = helper.equipments
                  result.size mustBe count
              }
          }
        }
      }
    }

    "equipment" - {
      "must return None" - {
        "when equipment is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.equipment(equipmentIndex)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when equipment is  defined and container id is undefined" in {
          val initialUserAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode(SealsBrokenOrTamperedCode, "test"))
            .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

          forAll(arbitraryEquipmentAnswers(initialUserAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val helper = IncidentAnswersHelper(userAnswers, mode, incidentIndex)
              val result = helper.equipment(index).get

              result.key.value mustBe "Transport equipment 1"
              result.value.value mustBe "Transport equipment 1"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
              action.visuallyHiddenText.get mustBe "transport equipment 1"
              action.id mustBe "change-transport-equipment-1"
          }
        }

        "when equipment is  defined and container id is defined" in {
          val initialUserAnswers = emptyUserAnswers
            .setValue(IncidentCodePage(incidentIndex), IncidentCode(SealsBrokenOrTamperedCode, "test"))
            .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

          forAll(arbitraryEquipmentAnswers(initialUserAnswers, incidentIndex, equipmentIndex), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val equipment = EquipmentDomain.userAnswersReader(incidentIndex, equipmentIndex).apply(Nil).run(userAnswers).value

              val helper = IncidentAnswersHelper(userAnswers, mode, incidentIndex)
              val result = helper.equipment(index).get

              result.key.value mustBe "Transport equipment 1"
              result.value.value mustBe s"Transport equipment 1 - container ${equipment.value.containerId.get}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe equipmentRoutes.CheckEquipmentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url
              action.visuallyHiddenText.get mustBe "transport equipment 1"
              action.id mustBe "change-transport-equipment-1"
          }
        }
      }
    }

    "addOrRemoveEquipments" - {
      "must return None" - {
        "when equipments array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.addOrRemoveEquipments
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when equipments array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(EquipmentSection(incidentIndex, Index(0)), Json.obj("foo" -> "bar"))
              val helper  = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result  = helper.addOrRemoveEquipments.get

              result.id mustBe "add-or-remove-transport-equipment"
              result.text mustBe "Add or remove transport equipment"
              result.href mustBe equipmentRoutes.AddAnotherEquipmentController.onPageLoad(answers.mrn, mode, incidentIndex).url
          }
        }
      }
    }

    "country" - {
      "must return None" - {
        "when IncidentCountryPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
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
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.code.get

              result.key.value mustBe "Incident code"
              result.value.value mustBe s"${code.code} - ${code.description}"
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
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
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

    "endorsementYesNo" - {
      "must return None" - {
        "when AddEndorsementPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
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

    "endorsementDate" - {
      "must return None" - {
        "when EndorsementDatePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementDate.get

              result.key.value mustBe "Endorsement date"
              result.value.value mustBe "9 September 2021"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe endorsementRoutes.EndorsementDateController.onPageLoad(answers.mrn, incidentIndex, mode).url
              action.visuallyHiddenText.get mustBe "endorsement date"
              action.id mustBe "change-endorsement-date"
          }
        }
      }
    }

    "endorsementAuthority" - {
      "must return None" - {
        "when EndorsementAuthorityPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementAuthority.get

              result.key.value mustBe "Authority"
              result.value.value mustBe authority
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe endorsementRoutes.EndorsementAuthorityController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "endorsement authority"
              action.id mustBe "change-endorsement-authority"
          }
        }
      }
    }

    "endorsementCountry" - {
      "must return None" - {
        "when EndorsementCountryPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementCountry.get

              result.key.value mustBe "Country"
              result.value.value mustBe country.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe endorsementRoutes.EndorsementCountryController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "endorsement country"
              action.id mustBe "change-endorsement-country"
          }
        }
      }
    }

    "endorsementLocation" - {
      "must return None" - {
        "when EndorsementLocationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.endorsementLocation.get

              result.key.value mustBe "Location"
              result.value.value mustBe location
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe endorsementRoutes.EndorsementLocationController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "endorsement location"
              action.id mustBe "change-endorsement-location"
          }
        }
      }
    }

    "qualifierOfIdentification" - {
      "must return None" - {
        "when QualifierOfIdentificationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.qualifierOfIdentification.get

              result.key.value mustBe "Identifier type"
              result.value.value mustBe identificationType.asString
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

    "unLocode" - {
      "must return None" - {
        "when UnLocodePage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.unLocode
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when UnLocodePage is defined" in {
          forAll(arbitrary[String], arbitrary[Mode]) {
            (unlocode, mode) =>
              val answers = emptyUserAnswers.setValue(UnLocodePage(incidentIndex), unlocode)

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.unLocode.get

              result.key.value mustBe "UN/LOCODE"
              result.value.value mustBe unlocode
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

    "coordinates" - {
      "must return None" - {
        "when CoordinatesPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
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

    "address" - {
      "must return None" - {
        "when AddressPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
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

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.address.get

              result.key.value mustBe "Address"
              result.value.value mustBe address.toString
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

    "containerIndicatorYesNo" - {
      "must return None" - {
        "when ContainerIndicatorYesNoPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.containerIndicatorYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when ContainerIndicatorYesNoPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(ContainerIndicatorYesNoPage(incidentIndex), true)

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.containerIndicatorYesNo.get

              result.key.value mustBe "Did the incident involve a container?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.ContainerIndicatorYesNoController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "if the incident involved a container"
              action.id mustBe "change-add-container-indicator"
          }
        }
      }
    }

    "transportEquipmentYesNo" - {
      "must return None" - {
        "when AddTransportEquipmentPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.transportEquipmentYesNo
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when AddTransportEquipmentPage defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(AddTransportEquipmentPage(incidentIndex), true)

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.transportEquipmentYesNo.get

              result.key.value mustBe "Do you need to add any transport equipment?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.AddTransportEquipmentController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "if you need to add any transport equipment"
              action.id mustBe "change-add-transport-equipment"
          }
        }
      }
    }

    "transportMeansIdentificationType" - {
      "must return None" - {
        "when IdentificationPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.transportMeansIdentificationType
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when IdentificationPage is defined" in {
          forAll(arbitrary[Identification], arbitrary[Mode]) {
            (identification, mode) =>
              val answers = emptyUserAnswers.setValue(IdentificationPage(incidentIndex), identification)

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.transportMeansIdentificationType.get

              result.key.value mustBe "Identification type"
              result.value.value mustBe identification.asString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe transportMeansRoutes.IdentificationController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "identification type for the replacement means of transport"
              action.id mustBe "change-transport-means-identification-type"
          }
        }
      }
    }

    "transportMeansIdentificationNumber" - {
      "must return None" - {
        "when IdentificationNumberPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.transportMeansIdentificationNumber
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when IdentificationNumberPage is defined" in {
          forAll(Gen.alphaNumStr, arbitrary[Mode]) {
            (identificationNumber, mode) =>
              val answers = emptyUserAnswers.setValue(IdentificationNumberPage(incidentIndex), identificationNumber)

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.transportMeansIdentificationNumber.get

              result.key.value mustBe "Identification"
              result.value.value mustBe identificationNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe transportMeansRoutes.IdentificationNumberController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "identification for the replacement means of transport"
              action.id mustBe "change-transport-means-identification-number"
          }
        }
      }
    }

    "transportMeansRegisteredCountry" - {
      "must return None" - {
        "when TransportNationalityPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentAnswersHelper(emptyUserAnswers, mode, incidentIndex)
              val result = helper.transportMeansRegisteredCountry
              result mustBe None
          }
        }
      }
      "must return Some(Row)" - {
        "when TransportNationalityPage is defined" in {
          forAll(arbitrary[Nationality], arbitrary[Mode]) {
            (nationality, mode) =>
              val answers = emptyUserAnswers.setValue(TransportNationalityPage(incidentIndex), nationality)

              val helper = IncidentAnswersHelper(answers, mode, incidentIndex)
              val result = helper.transportMeansRegisteredCountry.get

              result.key.value mustBe "Registered country"
              result.value.value mustBe nationality.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe transportMeansRoutes.TransportNationalityController.onPageLoad(answers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "registered country for the replacement means of transport"
              action.id mustBe "change-transport-means-registered-country"
          }
        }
      }
    }

  }
}
