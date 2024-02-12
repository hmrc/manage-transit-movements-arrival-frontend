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
import models.Index
import org.scalacheck.Gen
import pages.incident._
import pages.sections.incident.IncidentsSection

class IncidentsDomainSpec extends SpecBase with Generators {

  "IncidentDomainList" - {

    "can be parsed from UserAnswers" in {

      val numberOfIncidents = Gen.choose(1, frontendAppConfig.maxIncidents).sample.value

      val userAnswers = (0 until numberOfIncidents).foldLeft(emptyUserAnswers)({
        case (updatedUserAnswers, index) =>
          arbitraryIncidentAnswers(updatedUserAnswers, Index(index)).sample.value
      })

      val result = IncidentsDomain.userAnswersReader.apply(Nil).run(userAnswers)

      result.value.value.incidents.length mustBe numberOfIncidents
      result.value.pages.last mustBe IncidentsSection
    }

    "cannot be parsed from UserAnswer" - {

      "when there are no incidents" in {

        val result = IncidentsDomain.userAnswersReader.apply(Nil).run(emptyUserAnswers)

        result.left.value.page mustBe IncidentCountryPage(Index(0))
        result.left.value.pages mustBe Seq(
          IncidentCountryPage(Index(0))
        )
      }
    }
  }
}
