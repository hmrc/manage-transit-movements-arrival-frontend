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
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}

class AuthorisationsDomainSpec extends SpecBase with Generators {

  "AuthorisationDomain" - {

    "can be parsed from UserAnswers" - {

      "when there is at least one authorisation" in {
        val authorisationType = arbitrary[AuthorisationType].sample.value
        val referenceNumber   = Gen.alphaNumStr.sample.value

        val userAnswers = emptyUserAnswers
          .setValue(AuthorisationTypePage(authorisationIndex), authorisationType)
          .setValue(AuthorisationReferenceNumberPage(authorisationIndex), referenceNumber)

        val expectedResult = AuthorisationsDomain(
          authorisations = Seq(
            AuthorisationDomain(
              `type` = authorisationType,
              referenceNumber = referenceNumber
            )(authorisationIndex)
          )
        )

        val result: EitherType[AuthorisationsDomain] = UserAnswersReader[AuthorisationsDomain].run(userAnswers)

        result.value mustBe expectedResult
      }
    }

    "cannot be parsed from user answers" - {

      "when there is no authorisations" in {

        val result: EitherType[AuthorisationsDomain] = UserAnswersReader[AuthorisationsDomain].run(emptyUserAnswers)

        result.left.value.page mustBe AuthorisationTypePage(Index(0))
      }
    }
  }
}