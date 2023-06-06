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
import models.Index
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.CustomsOffice
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification._
import pages.identification.authorisation._

class IdentificationDomainSpec extends SpecBase with Generators {

  private val destinationOffice = arbitrary[CustomsOffice].sample.value

  "AuthorisationDomain" - {

    "can be parsed from UserAnswers" - {
      "when not a simplified journey" in {
        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IdentificationNumberPage, "identificationNumber")
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)

        val expectedResult = IdentificationDomain(
          mrn = userAnswers.mrn,
          destinationOffice = destinationOffice,
          identificationNumber = "identificationNumber",
          procedureType = ProcedureType.Normal,
          authorisationReferenceNumber = None
        )

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when a simplified journey and at least one authorisation" in {
        val authorisationType = arbitrary[AuthorisationType].sample.value
        val referenceNumber   = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IdentificationNumberPage, "identificationNumber")
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
          .setValue(AuthorisationTypePage, authorisationType)
          .setValue(AuthorisationReferenceNumberPage, referenceNumber)

        val expectedResult = IdentificationDomain(
          mrn = userAnswers.mrn,
          destinationOffice = destinationOffice,
          identificationNumber = "identificationNumber",
          procedureType = ProcedureType.Simplified,
          authorisationReferenceNumber = Some("referenceNumber")
        )

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {

      "when is simplified question unanswered" in {

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(emptyUserAnswers)

        result.left.value.page mustBe DestinationOfficePage
      }

      "when a simplified journey and no authorisations" in {

        val userAnswers = emptyUserAnswers
          .setValue(DestinationOfficePage, destinationOffice)
          .setValue(IdentificationNumberPage, "identificationNumber")
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.left.value.page mustBe AuthorisationTypePage
      }
    }
  }
}
