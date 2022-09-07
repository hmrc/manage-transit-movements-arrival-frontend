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

package models.journeyDomain.identification

import base.SpecBase
import generators.Generators
import models.Index
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification._
import pages.identification.authorisation.{_}

import java.time.LocalDate

class IdentificationDomainSpec extends SpecBase with Generators {

  "AuthorisationDomain" - {

    "can be parsed from UserAnswers" - {
      "when not a simplified journey" in {
        val date = arbitrary[LocalDate].sample.value
        val id   = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ArrivalDatePage, date)
          .setValue(IsSimplifiedProcedurePage, false)
          .setValue(IdentificationNumberPage, id)

        val expectedResult = IdentificationDomain(
          mrn = userAnswers.mrn,
          arrivalDate = date,
          isSimplified = false,
          authorisations = AuthorisationsDomain(Nil),
          identificationNumber = id
        )

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.value mustBe expectedResult
      }

      "when a simplified journey and at least one authorisation" in {
        val date              = arbitrary[LocalDate].sample.value
        val authorisationType = arbitrary[AuthorisationType].sample.value
        val referenceNumber   = Gen.alphaNumStr.sample.value
        val id                = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ArrivalDatePage, date)
          .setValue(IsSimplifiedProcedurePage, true)
          .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), referenceNumber)
          .setValue(IdentificationNumberPage, id)

        val expectedResult = IdentificationDomain(
          mrn = userAnswers.mrn,
          arrivalDate = date,
          isSimplified = true,
          authorisations = AuthorisationsDomain(
            Seq(
              AuthorisationDomain(
                `type` = authorisationType,
                referenceNumber = referenceNumber
              )(authorisationIndex)
            )
          ),
          identificationNumber = id
        )

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {

      "when arrival date unanswered" in {
        val userAnswers = emptyUserAnswers

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.left.value.page mustBe ArrivalDatePage
      }

      "when is simplified question unanswered" in {
        val date = arbitrary[LocalDate].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ArrivalDatePage, date)

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.left.value.page mustBe IsSimplifiedProcedurePage
      }

      "when a simplified journey and no authorisations" in {
        val date = arbitrary[LocalDate].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ArrivalDatePage, date)
          .setValue(IsSimplifiedProcedurePage, true)

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.left.value.page mustBe AuthorisationTypePage(Index(0))
      }

      "when identification number unanswered" in {
        val date = arbitrary[LocalDate].sample.value

        val userAnswers = emptyUserAnswers
          .setValue(ArrivalDatePage, date)
          .setValue(IsSimplifiedProcedurePage, false)

        val result: EitherType[IdentificationDomain] = UserAnswersReader[IdentificationDomain].run(userAnswers)

        result.left.value.page mustBe IdentificationNumberPage
      }
    }
  }
}
