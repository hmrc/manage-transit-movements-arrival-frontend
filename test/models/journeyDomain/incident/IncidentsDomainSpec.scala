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
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.Index
import models.journeyDomain.{EitherType, UserAnswersReader}
import org.scalacheck.Gen
import pages.incident._

class IncidentsDomainSpec extends SpecBase with Generators with ArrivalUserAnswersGenerator {

  "IncidentDomainList" - {

    "can be parsed from UserAnswers" in {

      val numberOfIncidents = Gen.choose(1, frontendAppConfig.maxIncidents).sample.value

      val userAnswers = (0 until numberOfIncidents).foldLeft(emptyUserAnswers)({
        case (updatedUserAnswers, index) =>
          arbitraryIncidentAnswers(updatedUserAnswers, Index(index)).sample.value
      })

      val result: EitherType[IncidentsDomain] = UserAnswersReader[IncidentsDomain].run(userAnswers)

      result.value.incidentsDomain.length mustBe numberOfIncidents

    }

    "cannot be parsed from UserAnswer" - {

      "when there are no incidents" in {

        val result: EitherType[IncidentsDomain] = UserAnswersReader[IncidentsDomain].run(emptyUserAnswers)

        result.left.value.page mustBe IncidentCountryPage(Index(0))
      }
    }
  }

}
