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
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.incident.{IncidentCountryPage, IncidentFlagPage}

class IncidentDomainSpec extends SpecBase with Generators {

  private val country = arbitrary[Country].sample.value

  "IncidentDomain" - {

    "can be parsed from UserAnswers" in {

      val userAnswers = emptyUserAnswers
        .setValue(IncidentFlagPage, true)
        .setValue(IncidentCountryPage, country)

      val expectedResult = IncidentDomain(incidentCountry = Some(country))

      val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain].run(userAnswers)

      result.value mustBe expectedResult

    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain].run(emptyUserAnswers)

        result.left.value.page mustBe IncidentFlagPage
      }
    }
  }

}
