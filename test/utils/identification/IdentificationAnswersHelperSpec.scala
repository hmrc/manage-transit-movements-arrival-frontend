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

package utils.identification

import base.SpecBase
import controllers.identification.routes
import generators.Generators
import models.identification.ProcedureType
import models.reference.CustomsOffice
import models.{Mode, MovementReferenceNumber}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification._

class IdentificationAnswersHelperSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "IdentificationAnswersHelper" - {

    "movementReferenceNumber" - {
      "must return row" in {
        forAll(arbitrary[MovementReferenceNumber], arbitrary[Mode]) {
          (mrn, mode) =>
            val answers = emptyUserAnswers.copy(mrn = mrn)

            val helper = IdentificationAnswersHelper(answers, mode)
            val result = helper.movementReferenceNumber

            result.key.value mustBe "Movement Reference Number (MRN)"
            result.value.value mustBe mrn.toString
            val actions = result.actions.get.items
            actions.size mustBe 1
            val action = actions.head
            action.content.value mustBe "Change"
            action.href mustBe routes.MovementReferenceNumberController.onPageLoad(mode).url
            action.visuallyHiddenText.get mustBe "Movement Reference Number (MRN)"
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
              result mustBe None
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

              result.key.value mustBe "Office of destination"
              result.value.value mustBe customsOffice.toString
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.DestinationOfficeController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "office of destination"
              action.id mustBe "change-destination-office"
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
              result mustBe None
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

              result.key.value mustBe "What is the EORI number or TIN for the consignee?"
              result.value.value mustBe identificationNumber
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IdentificationNumberController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "identification number"
              action.id mustBe "change-identification-number"
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
              result mustBe None
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

              result.key.value mustBe "Procedure type"
              val key = s"identification.isSimplifiedProcedure.$procedureType"
              messages.isDefinedAt(key) mustBe true
              result.value.value mustBe messages(key)
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IsSimplifiedProcedureController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "procedure type"
              action.id mustBe "change-is-simplified-procedure"
          }
        }
      }
    }
  }
}
