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

package models.journeyDomain.incident.endorsement

import base.SpecBase
import generators.Generators
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.incident.endorsement.{EndorsementAuthorityPage, EndorsementCountryPage, EndorsementDatePage, EndorsementLocationPage}

import java.time.LocalDate

class EndorsementDomainSpec extends SpecBase with Generators {

  private val localDate = LocalDate.now()
  private val country   = arbitrary[Country].sample.value
  private val authority = Gen.alphaNumStr.sample.value
  private val location  = Gen.alphaNumStr.sample.value

  "IncidentDomain" - {

    "can be parsed from UserAnswers" in {

      val userAnswers = emptyUserAnswers
        .setValue(EndorsementDatePage(index), localDate)
        .setValue(EndorsementAuthorityPage(index), authority)
        .setValue(EndorsementCountryPage(index), country)
        .setValue(EndorsementLocationPage(index), location)

      val expectedResult = EndorsementDomain(localDate, authority, country, location)

      val result = EndorsementDomain.userAnswersReader(index).apply(Nil).run(userAnswers)

      result.value.value mustBe expectedResult
      result.value.pages mustBe Seq(
        EndorsementDatePage(index),
        EndorsementAuthorityPage(index),
        EndorsementCountryPage(index),
        EndorsementLocationPage(index)
      )
    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Seq[QuestionPage[?]] = Seq(
          EndorsementDatePage(index),
          EndorsementAuthorityPage(index),
          EndorsementCountryPage(index),
          EndorsementLocationPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(EndorsementDatePage(index), localDate)
          .setValue(EndorsementAuthorityPage(index), authority)
          .setValue(EndorsementCountryPage(index), country)
          .setValue(EndorsementLocationPage(index), location)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers = userAnswers.removeValue(mandatoryPage)

            val result = EndorsementDomain.userAnswersReader(index).apply(Nil).run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
