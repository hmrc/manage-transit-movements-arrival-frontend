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

package models.journeyDomain.identification

import base.SpecBase
import generators.Generators
import models.identification.ProcedureType
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification._

class IdentificationDomainSpec extends SpecBase with Generators {

  private val destinationOffice = arbitrary[CustomsOffice].sample.value

  "AuthorisationDomain" - {

    "can be parsed from UserAnswers" - {
      "when not a simplified journey" in {
        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
          .setValue(IdentificationNumberPage, "identificationNumber")

        val expectedResult = IdentificationDomain(
          mrn = userAnswers.mrn,
          destinationOffice = destinationOffice,
          procedureType = ProcedureType.Normal,
          identificationNumber = "identificationNumber",
          authorisationReferenceNumber = None
        )

        val result = IdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          DestinationOfficePage,
          IsSimplifiedProcedurePage,
          IdentificationNumberPage
        )
      }

      "when a simplified journey and at least one authorisation" in {
        val referenceNumber = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
          .setValue(IdentificationNumberPage, "identificationNumber")
          .setValue(AuthorisationReferenceNumberPage, referenceNumber)

        val expectedResult = IdentificationDomain(
          mrn = userAnswers.mrn,
          destinationOffice = destinationOffice,
          procedureType = ProcedureType.Simplified,
          identificationNumber = "identificationNumber",
          authorisationReferenceNumber = Some(referenceNumber)
        )

        val result = IdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          DestinationOfficePage,
          IsSimplifiedProcedurePage,
          IdentificationNumberPage,
          AuthorisationReferenceNumberPage
        )
      }
    }

    "cannot be parsed from user answers" - {
      "when is simplified question unanswered" in {
        val result = IdentificationDomain.userAnswersReader.apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe DestinationOfficePage
        result.left.value.pages mustBe Seq(
          DestinationOfficePage
        )
      }

      "when a simplified journey and no authorisations" in {
        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
          .setValue(IdentificationNumberPage, "identificationNumber")

        val result = IdentificationDomain.userAnswersReader.apply(Nil).run(userAnswers)

        result.left.value.page mustBe AuthorisationReferenceNumberPage
        result.left.value.pages mustBe Seq(
          DestinationOfficePage,
          IsSimplifiedProcedurePage,
          IdentificationNumberPage,
          AuthorisationReferenceNumberPage
        )
      }
    }
  }
}
