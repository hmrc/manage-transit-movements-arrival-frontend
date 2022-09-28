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
import models.Index
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

class IncidentDomainListSpec extends SpecBase with Generators {

  private val country      = arbitrary[Country].sample.value
  private val incidentCode = arbitrary[IncidentCode].sample.value
  private val incidentText = Gen.alphaNumStr.sample.value.take(Constants.maxIncidentTextLength)
  private val index1       = Index(0)
  private val index2       = Index(1)
  private val localDate    = LocalDate.now()
  private val authority    = Gen.alphaNumStr.sample.value
  private val place        = Gen.alphaNumStr.sample.value

  "IncidentDomainList" - {

    "can be parsed from UserAnswers" in {

      val userAnswers = emptyUserAnswers
        .setValue(IncidentCountryPage(index1), country)
        .setValue(IncidentCodePage(index1), incidentCode)
        .setValue(IncidentTextPage(index1), incidentText)
        .setValue(AddEndorsementPage(index1), true)
        .setValue(EndorsementDatePage(index1), localDate)
        .setValue(EndorsementAuthorityPage(index1), authority)
        .setValue(EndorsementPlacePage(index1), place)
        .setValue(EndorsementCountryPage(index1), country)
        .setValue(IncidentCountryPage(index2), country)
        .setValue(IncidentCodePage(index2), incidentCode)
        .setValue(IncidentTextPage(index2), incidentText)
        .setValue(AddEndorsementPage(index2), true)
        .setValue(EndorsementDatePage(index2), localDate)
        .setValue(EndorsementAuthorityPage(index2), authority)
        .setValue(EndorsementPlacePage(index2), place)
        .setValue(EndorsementCountryPage(index2), country)

      val expectedResult = IncidentDomainList(
        Seq(
          IncidentDomain(
            incidentCountry = country,
            incidentCode = incidentCode,
            incidentText = incidentText,
            endorsement = Some(EndorsementDomain(localDate, authority, place, country))
          ),
          IncidentDomain(
            incidentCountry = country,
            incidentCode = incidentCode,
            incidentText = incidentText,
            endorsement = Some(EndorsementDomain(localDate, authority, place, country))
          )
        )
      )

      val result: EitherType[IncidentDomainList] = UserAnswersReader[IncidentDomainList].run(userAnswers)

      result.value mustBe expectedResult

    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val userAnswers = emptyUserAnswers
          .setValue(IncidentCountryPage(index1), country)
          .setValue(IncidentCodePage(index1), incidentCode)
          .setValue(IncidentTextPage(index1), incidentText)
          .setValue(AddEndorsementPage(index1), true)
          .setValue(EndorsementDatePage(index1), localDate)
          .setValue(EndorsementAuthorityPage(index1), authority)
          .setValue(IncidentCountryPage(index2), country)
          .setValue(IncidentCodePage(index2), incidentCode)
          .setValue(IncidentTextPage(index2), incidentText)
          .setValue(AddEndorsementPage(index2), true)
          .setValue(EndorsementDatePage(index2), localDate)
          .setValue(EndorsementAuthorityPage(index2), authority)

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(IncidentCountryPage(index1), IncidentCodePage(index1), IncidentTextPage(index1))

        mandatoryPages.map {

          mandatoryPage =>
            val updatedUserAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[IncidentDomainList] = UserAnswersReader[IncidentDomainList].run(updatedUserAnswers)

            result.left.value.page mustBe mandatoryPage
        }
      }
    }
  }

}
