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
import generators.Generators
import models.Index
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.{Country, IncidentCode}
import org.scalacheck.Arbitrary.arbitrary
import pages.QuestionPage
import pages.incident.{IncidentCodePage, IncidentCountryPage}

class IncidentDomainListSpec extends SpecBase with Generators {

  private val country      = arbitrary[Country].sample.value
  private val incidentCode = arbitrary[IncidentCode].sample.value
  private val index1       = Index(0)
  private val index2       = Index(1)

  "IncidentDomainList" - {

    "can be parsed from UserAnswers" in {

      val userAnswers = emptyUserAnswers
        .setValue(IncidentCountryPage(index1), country)
        .setValue(IncidentCodePage(index1), incidentCode)
        .setValue(IncidentCountryPage(index2), country)
        .setValue(IncidentCodePage(index2), incidentCode)

      val expectedResult = IncidentDomainList(
        Seq(
          IncidentDomain(incidentCountry = country, incidentCode = incidentCode),
          IncidentDomain(incidentCountry = country, incidentCode = incidentCode)
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
          .setValue(IncidentCountryPage(index2), country)
          .setValue(IncidentCodePage(index2), incidentCode)

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(IncidentCountryPage(index1), IncidentCodePage(index1))

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
