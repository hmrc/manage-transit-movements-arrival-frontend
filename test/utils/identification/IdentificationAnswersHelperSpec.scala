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

package utils.identification

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.identification.routes
import generators.Generators
import models.identification.ProcedureType
import models.reference.CustomsOffice
import models.{Mode, MovementReferenceNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.*

class IdentificationAnswersHelperSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  "IdentificationAnswersHelper" - {

    "movementReferenceNumber" - {
      "must return row" in {
        forAll(arbitrary[MovementReferenceNumber], arbitrary[Mode]) {
          (mrn, mode) =>
            val answers = emptyUserAnswers.copy(mrn = mrn)

            val helper = IdentificationAnswersHelper(answers, mode)
            val result = helper.movementReferenceNumber

            result.key.value mustEqual "Movement Reference Number (MRN)"
            result.value.value mustEqual mrn.toString
            val actions = result.actions.get.items
            actions.size mustEqual 1
            val action = actions.head
            action.content.value mustEqual "Change"
            action.href mustEqual routes.MovementReferenceNumberController.onPageReload(mrn).url
            action.visuallyHiddenText.get mustEqual "Movement Reference Number (MRN)"
        }
      }
    }

    "destinationOffice" - {
      "must return None" - {
        "when DestinationOfficePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.destinationOffice
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when DestinationOfficePage defined" in {
          forAll(arbitrary[CustomsOffice], arbitrary[Mode]) {
            (customsOffice, mode) =>
              val answers = emptyUserAnswers.setValue(DestinationOfficePage, customsOffice)

              val helper = IdentificationAnswersHelper(answers, mode)
              val result = helper.destinationOffice.get

              result.key.value mustEqual "Office of destination"
              result.value.value mustEqual customsOffice.toString
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.DestinationOfficeController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "office of destination"
              action.id mustEqual "change-destination-office"
          }
        }
      }
    }

    "identificationNumber" - {
      "must return None" - {
        "when IdentificationNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IdentificationAnswersHelper(emptyUserAnswers, mode)
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

              val helper = IdentificationAnswersHelper(answers, mode)
              val result = helper.identificationNumber.get

              result.key.value mustEqual "Consignee EORI number or Trader Identification Number (TIN)"
              result.value.value mustEqual identificationNumber
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.IdentificationNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "consignee EORI number or Trader Identification Number (TIN)"
              action.id mustEqual "change-identification-number"
          }
        }
      }
    }

    "isSimplified" - {
      "must return None" - {
        "when IsSimplifiedProcedurePage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.isSimplified
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when IsSimplifiedProcedurePage defined" in {
          forAll(arbitrary[ProcedureType], arbitrary[Mode]) {
            (procedureType, mode) =>
              val answers = emptyUserAnswers.setValue(IsSimplifiedProcedurePage, procedureType)

              val helper = IdentificationAnswersHelper(answers, mode)
              val result = helper.isSimplified.get

              result.key.value mustEqual "Procedure type"
              val key = s"identification.isSimplifiedProcedure.$procedureType"
              messages.isDefinedAt(key) mustEqual true
              result.value.value mustEqual messages(s"$key")
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.IsSimplifiedProcedureController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "procedure type"
              action.id mustEqual "change-is-simplified-procedure"
          }
        }
      }
    }

    "authorisationReferenceNumber" - {
      "must return None" - {
        "when AuthorisationReferenceNumberPage undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IdentificationAnswersHelper(emptyUserAnswers, mode)
              val result = helper.authorisationReferenceNumber
              result must not be defined
          }
        }
      }

      "must return Some(Row)" - {
        "when AuthorisationReferenceNumberPage defined" in {
          forAll(arbitrary[Mode], Gen.alphaNumStr) {
            (mode, ref) =>
              val answers = emptyUserAnswers.setValue(AuthorisationReferenceNumberPage, ref)

              val helper = IdentificationAnswersHelper(answers, mode)
              val result = helper.authorisationReferenceNumber.get

              result.key.value mustEqual "Authorisation reference number"
              result.value.value mustEqual ref
              val actions = result.actions.get.items
              actions.size mustEqual 1
              val action = actions.head
              action.content.value mustEqual "Change"
              action.href mustEqual routes.AuthorisationReferenceNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustEqual "authorisation reference number"
              action.id mustEqual "change-authorisation-reference-number"
          }
        }
      }
    }
  }
}
