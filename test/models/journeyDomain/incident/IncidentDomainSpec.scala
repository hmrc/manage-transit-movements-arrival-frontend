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

package models.journeyDomain.incident

import base.SpecBase
import forms.Constants
import generators.Generators
import models.journeyDomain.incident.endorsement.EndorsementDomain
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.{Country, IncidentCode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.QuestionPage
import pages.incident.{
  AddEndorsementPage,
  EndorsementAuthorityPage,
  EndorsementCountryPage,
  EndorsementDatePage,
  EndorsementPlacePage,
  IncidentCodePage,
  IncidentCountryPage,
  IncidentTextPage
}

import java.time.LocalDate

class IncidentDomainSpec extends SpecBase with Generators {

  private val country      = arbitrary[Country].sample.value
  private val incidentCode = arbitrary[IncidentCode].sample.value
  private val incidentText = Gen.alphaNumStr.sample.value.take(Constants.maxIncidentTextLength)
  private val localDate    = LocalDate.now()
  private val authority    = Gen.alphaNumStr.sample.value
  private val place        = Gen.alphaNumStr.sample.value

  "IncidentDomain" - {

    "can be parsed from UserAnswers when addEndorsement is true" in {

      val userAnswers = emptyUserAnswers
        .setValue(IncidentCountryPage(index), country)
        .setValue(IncidentCodePage(index), incidentCode)
        .setValue(IncidentTextPage(index), incidentText)
        .setValue(AddEndorsementPage(index), true)
        .setValue(EndorsementDatePage(index), localDate)
        .setValue(EndorsementAuthorityPage(index), authority)
        .setValue(EndorsementPlacePage(index), place)
        .setValue(EndorsementCountryPage(index), country)

      val expectedResult = IncidentDomain(
        incidentCountry = country,
        incidentCode = incidentCode,
        incidentText = incidentText,
        endorsement = Some(EndorsementDomain(localDate, authority, place, country))
      )

      val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(index)).run(userAnswers)

      result.value mustBe expectedResult

    }

    "can be parsed from UserAnswers when addEndorsement is false" in {

      val userAnswers = emptyUserAnswers
        .setValue(IncidentCountryPage(index), country)
        .setValue(IncidentCodePage(index), incidentCode)
        .setValue(IncidentTextPage(index), incidentText)
        .setValue(AddEndorsementPage(index), false)

      val expectedResult = IncidentDomain(
        incidentCountry = country,
        incidentCode = incidentCode,
        incidentText = incidentText,
        endorsement = None
      )

      val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(index)).run(userAnswers)

      result.value mustBe expectedResult

    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing and addEndorsement is true" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          IncidentCountryPage(index),
          IncidentCodePage(index),
          IncidentTextPage(index),
          AddEndorsementPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(IncidentCountryPage(index), country)
          .setValue(IncidentCodePage(index), incidentCode)
          .setValue(IncidentTextPage(index), incidentText)
          .setValue(AddEndorsementPage(index), true)
          .setValue(EndorsementDatePage(index), localDate)
          .setValue(EndorsementAuthorityPage(index), authority)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(index)).run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }

      }

      "when a mandatory page is missing and addEndorsement is false" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          IncidentCountryPage(index),
          IncidentCodePage(index),
          IncidentTextPage(index),
          AddEndorsementPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(IncidentCountryPage(index), country)
          .setValue(IncidentCodePage(index), incidentCode)
          .setValue(IncidentTextPage(index), incidentText)
          .setValue(AddEndorsementPage(index), false)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(index)).run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }

      }
    }
  }

}
