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

package utils.incident

import base.SpecBase
import controllers.incident.routes
import generators.Generators
import models.incident.IncidentCode
import models.journeyDomain.UserAnswersReader
import models.journeyDomain.incident.IncidentDomain
import models.reference.Country
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident.{IncidentCodePage, IncidentCountryPage, IncidentFlagPage}
import pages.sections.incident.IncidentSection
import play.api.libs.json.Json
import viewModels.ListItem

class IncidentsAnswersHelperSpec extends SpecBase with Generators {

  "IncidentsAnswersHelper" - {

    "incidents" - {
      "must return no rows" - {
        "when no incidents defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.incidents
              result mustBe Nil
          }
        }
      }

      "must return rows" - {
        "when incidents defined" in {
          forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxIncidents)) {
            (mode, count) =>
              val userAnswersGen = (0 until count).foldLeft(Gen.const(emptyUserAnswers)) {
                (acc, i) =>
                  acc.flatMap(arbitraryIncidentAnswers(_, Index(i)))
              }
              forAll(userAnswersGen) {
                userAnswers =>
                  val helper = IncidentsAnswersHelper(userAnswers, mode)
                  val result = helper.incidents
                  result.size mustBe count
              }
          }
        }
      }
    }

    "addOrRemoveIncidents" - {
      "must return None" - {
        "when incidents array is empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.addOrRemoveIncidents()
              result mustBe None
          }
        }
      }

      "must return Some(Link)" - {
        "when incidents array is non-empty" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(IncidentSection(Index(0)), Json.obj("foo" -> "bar"))
              val helper  = IncidentsAnswersHelper(answers, mode)
              val result  = helper.addOrRemoveIncidents().get

              result.id mustBe "add-or-remove-incidents"
              result.text mustBe "Add or remove incidents"
              result.href mustBe routes.AddAnotherIncidentController.onPageLoad(answers.mrn, mode).url
          }
        }
      }
    }

    "incident" - {
      "must return None" - {
        "when incident is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = IncidentsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.incident(index)
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when incident is defined" in {
          forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val incident = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(incidentIndex)).run(userAnswers).value

              val helper = IncidentsAnswersHelper(userAnswers, mode)
              val result = helper.incident(index).get

              result.key.value mustBe s"Incident ${incidentIndex.display}"
              result.value.value mustBe s"Incident ${incidentIndex.display} - ${incident.incidentCode}"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.CheckIncidentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url
              action.visuallyHiddenText.get mustBe "incident 1"
              action.id mustBe "change-incident-1"
          }
        }
      }
    }

    "incident-flag" - {
      "must return None" - {
        "when IncidentFlagPage is undefined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val helper = new IncidentsAnswersHelper(emptyUserAnswers, mode)
              val result = helper.incidentFlag
              result mustBe None
          }
        }
      }

      "must return Some(Row)" - {
        "when IncidentFlagPage is defined" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val answers = emptyUserAnswers.setValue(IncidentFlagPage, true)

              val helper = new IncidentsAnswersHelper(answers, mode)
              val result = helper.incidentFlag.get

              result.key.value mustBe "Were there any incidents during the transit?"
              result.value.value mustBe "Yes"
              val actions = result.actions.get.items
              actions.size mustBe 1
              val action = actions.head
              action.content.value mustBe "Change"
              action.href mustBe routes.IncidentFlagController.onPageLoad(answers.mrn, mode).url
              action.visuallyHiddenText.get mustBe "if there were any incidents during the transit"
              action.id mustBe "change-incident-flag"
          }
        }
      }
    }

    "listItems" - {

      "when empty user answers" - {
        "must return empty list of list items" in {
          forAll(arbitrary[Mode]) {
            mode =>
              val userAnswers = emptyUserAnswers

              val helper = IncidentsAnswersHelper(userAnswers, mode)
              helper.listItems mustBe Nil
          }
        }
      }

      "when user answers populated with a complete incident" - {
        "must return one list item" in {
          forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex), arbitrary[Mode]) {
            (userAnswers, mode) =>
              val incident = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(incidentIndex)).run(userAnswers).value
              val helper   = IncidentsAnswersHelper(userAnswers, mode)
              helper.listItems mustBe Seq(
                Right(
                  ListItem(
                    name = s"Incident ${incidentIndex.display} - ${incident.incidentCode}",
                    changeUrl = routes.CheckIncidentAnswersController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url,
                    removeUrl = Some(routes.ConfirmRemoveIncidentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url)
                  )
                )
              )
          }
        }
      }

      "when user answers populated with an in progress incident" - {
        "when incident code is undefined" - {
          "must return one list item with just the index as the name" in {
            forAll(arbitrary[Country], arbitrary[Mode]) {
              (country, mode) =>
                val userAnswers = emptyUserAnswers.setValue(IncidentCountryPage(incidentIndex), country)

                val helper = IncidentsAnswersHelper(userAnswers, mode)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = s"Incident ${incidentIndex.display}",
                      changeUrl = routes.IncidentCodeController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url,
                      removeUrl = Some(routes.ConfirmRemoveIncidentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url)
                    )
                  )
                )
            }
          }
        }

        "when incident code is defined" - {
          "must return one list item with the index and the incident code as the name" in {
            forAll(arbitrary[Country], arbitrary[IncidentCode], arbitrary[Mode]) {
              (country, incidentCode, mode) =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCountryPage(incidentIndex), country)
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)

                val helper = IncidentsAnswersHelper(userAnswers, mode)
                helper.listItems mustBe Seq(
                  Left(
                    ListItem(
                      name = s"Incident ${incidentIndex.display} - $incidentCode",
                      changeUrl = routes.IncidentTextController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url,
                      removeUrl = Some(routes.ConfirmRemoveIncidentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url)
                    )
                  )
                )
            }
          }
        }
      }
    }
  }

}
