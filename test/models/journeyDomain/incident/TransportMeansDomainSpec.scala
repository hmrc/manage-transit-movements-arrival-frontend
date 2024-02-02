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

package models.journeyDomain.incident

import base.SpecBase
import generators.Generators
import models.reference.{Identification, Nationality}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident.transportMeans._

class TransportMeansDomainSpec extends SpecBase with Generators {

  private val identification       = arbitrary[Identification].sample.value
  private val identificationNumber = Gen.alphaNumStr.sample.value
  private val nationality          = arbitrary[Nationality].sample.value

  "TransportMeansDomain" - {

    "can be parsed from user answers" - {
      "when all answers answered" in {

        val userAnswers = emptyUserAnswers
          .setValue(IdentificationPage(incidentIndex), identification)
          .setValue(IdentificationNumberPage(incidentIndex), identificationNumber)
          .setValue(TransportNationalityPage(incidentIndex), nationality)

        val expectedResult = TransportMeansDomain(
          identificationType = identification,
          identificationNumber = identificationNumber,
          nationality = nationality
        )

        val result = TransportMeansDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

        result.value.value mustBe expectedResult
        result.value.pages mustBe Seq(
          IdentificationPage(incidentIndex),
          IdentificationNumberPage(incidentIndex),
          TransportNationalityPage(incidentIndex)
        )
      }
    }

    "cannot be parsed from user answers" - {
      "when transport means type is unanswered" in {

        val result = TransportMeansDomain.userAnswersReader(incidentIndex).apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe IdentificationPage(incidentIndex)
        result.left.value.pages mustBe Seq(
          IdentificationPage(incidentIndex)
        )
      }

      "when transport means id number is unanswered" in {

        val userAnswers = emptyUserAnswers
          .setValue(IdentificationPage(incidentIndex), identification)

        val result = TransportMeansDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe IdentificationNumberPage(incidentIndex)
        result.left.value.pages mustBe Seq(
          IdentificationPage(incidentIndex),
          IdentificationNumberPage(incidentIndex)
        )
      }

      "when transport means nationality is unanswered" in {

        val userAnswers = emptyUserAnswers
          .setValue(IdentificationPage(incidentIndex), identification)
          .setValue(IdentificationNumberPage(incidentIndex), identificationNumber)

        val result = TransportMeansDomain.userAnswersReader(incidentIndex).apply(Nil).run(userAnswers)

        result.left.value.page mustBe TransportNationalityPage(incidentIndex)
        result.left.value.pages mustBe Seq(
          IdentificationPage(incidentIndex),
          IdentificationNumberPage(incidentIndex),
          TransportNationalityPage(incidentIndex)
        )
      }
    }
  }
}
