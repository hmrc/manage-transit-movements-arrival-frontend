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
import pages.QuestionPage
import pages.incident.EndorsementDatePage

import java.time.LocalDate

class EndorsementDomainSpec extends SpecBase with Generators {

  private val localDate = LocalDate.now()

  "IncidentDomain" - {

    "can be parsed from UserAnswers" in {

      val userAnswers = emptyUserAnswers
        .setValue(EndorsementDatePage(index), localDate)

      val expectedResult = EndorsementDomain(localDate)

      val result: EitherType[EndorsementDomain] = UserAnswersReader[EndorsementDomain](EndorsementDomain.userAnswersReader(index)).run(userAnswers)

      result.value mustBe expectedResult

    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          EndorsementDatePage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(EndorsementDatePage(index), localDate)

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
