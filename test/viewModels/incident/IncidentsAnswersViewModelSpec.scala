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

package viewModels.incident

import base.SpecBase
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.{Index, Mode, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.Assertion
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.IncidentFlagPage
import viewModels.incident.IncidentsAnswersViewModel.IncidentsAnswersViewModelProvider
import viewModels.sections.Section

class IncidentsAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  private val sectionTitle = "Incidents"

  private def checkAddAnotherLink(section: Section, userAnswers: UserAnswers, mode: Mode): Assertion = {
    val addOrRemoveIncidentsLink = section.addAnotherLink.value
    addOrRemoveIncidentsLink.text mustBe "Add or remove incidents"
    addOrRemoveIncidentsLink.id mustBe "add-or-remove-incidents"
    addOrRemoveIncidentsLink.href mustBe controllers.incident.routes.AddAnotherIncidentController.onPageLoad(userAnswers.mrn, mode).url
  }

  "incidents section" - {
    "when there was an incident" - {
      "must return 1 row plus a row for each incident" in {
        forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxIncidents)) {
          (mode, numberOfIncidents) =>
            val initialAnswers = emptyUserAnswers.setValue(IncidentFlagPage, true)
            val userAnswersGen = (0 until numberOfIncidents).foldLeft(Gen.const(initialAnswers)) {
              (acc, i) =>
                acc.flatMap(arbitraryIncidentAnswers(_, Index(i)))
            }
            forAll(userAnswersGen) {
              userAnswers =>
                val section = new IncidentsAnswersViewModelProvider().apply(userAnswers, mode).section
                section.sectionTitle.get mustBe sectionTitle
                section.rows.size mustBe 1 + numberOfIncidents
                checkAddAnotherLink(section, userAnswers, mode)
            }
        }
      }
    }

    "when there wasn't an incident" - {
      "must return 1 row" in {
        val userAnswers = emptyUserAnswers.setValue(IncidentFlagPage, false)
        forAll(arbitrary[Mode]) {
          mode =>
            val section = new IncidentsAnswersViewModelProvider().apply(userAnswers, mode).section
            section.sectionTitle.get mustBe sectionTitle
            section.rows.size mustBe 1
            checkAddAnotherLink(section, userAnswers, mode)
        }
      }
    }
  }
}
