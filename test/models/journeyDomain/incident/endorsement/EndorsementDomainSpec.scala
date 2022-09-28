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

package models.journeyDomain.incident.endorsement

import base.SpecBase
import generators.Generators
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.incident.{EndorsementAuthorityPage, EndorsementDatePage, EndorsementPlacePage, EndorsementCountryPage}

import java.time.LocalDate

class EndorsementDomainSpec extends SpecBase with Generators {

  private val localDate = LocalDate.now()
  private val country   = arbitrary[Country].sample.value
  private val authority = Gen.alphaNumStr.sample.value
  private val place     = Gen.alphaNumStr.sample.value

  "IncidentDomain" - {

    "can be parsed from UserAnswers" in {

      val userAnswers = emptyUserAnswers
        .setValue(EndorsementDatePage(index), localDate)
        .setValue(EndorsementAuthorityPage(index), authority)
        .setValue(EndorsementPlacePage(index), place)
        .setValue(EndorsementCountryPage(index), country)

      val expectedResult = EndorsementDomain(localDate, authority, place, country)

      val result: EitherType[EndorsementDomain] = UserAnswersReader[EndorsementDomain](EndorsementDomain.userAnswersReader(index)).run(userAnswers)

      result.value mustBe expectedResult

    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          EndorsementDatePage(index),
          EndorsementAuthorityPage(index),
          EndorsementPlacePage(index),
          EndorsementCountryPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(EndorsementDatePage(index), localDate)
          .setValue(EndorsementAuthorityPage(index), authority)
          .setValue(EndorsementPlacePage(index), place)
          .setValue(EndorsementCountryPage(index), country)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[EndorsementDomain] = UserAnswersReader[EndorsementDomain](EndorsementDomain.userAnswersReader(index)).run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }
}
