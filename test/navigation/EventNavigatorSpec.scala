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

package navigation

import base.SpecBase
import controllers.events.seals.{routes => sealRoutes}
import controllers.events.transhipments.{routes => transhipmentRoutes}
import controllers.events.{routes => eventRoutes}
import controllers.routes
import generators.Generators
import models.TranshipmentType.{DifferentContainer, DifferentContainerAndVehicle, DifferentVehicle}
import models._
import models.domain.ContainerDomain
import models.messages.EnRouteEvent
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.events._
import pages.events.transhipments.{ContainerNumberPage, TranshipmentTypePage}
import pages.{IncidentOnRoutePage, Page}
import queries.{ContainersQuery, EventsQuery}

class EventNavigatorSpec extends SpecBase with Generators {

  private val navigator: Navigator = new EventNavigator()

  private val country: CountryCode = CountryCode("GB")

  "Event Navigator" - {

    "in Normal mode" - {

      val mode = NormalMode

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(UnknownPage, mode, answers)
              .mustBe(routes.MovementReferenceNumberController.onPageLoad())
        }
      }

      "must go from Event Country to Event Place" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EventCountryPage(eventIndex), mode, answers)
              .mustBe(eventRoutes.EventPlaceController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
        }
      }

      "must go from Event Place to Event Reported" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EventPlacePage(eventIndex), mode, answers)
              .mustBe(eventRoutes.EventReportedController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
        }
      }

      "must go from Event Reported to Is Transhipment" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EventReportedPage(eventIndex), mode, answers)
              .mustBe(eventRoutes.IsTranshipmentController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
        }
      }

      "must go from Is Transhipment" - {

        "to Incident Information when the event has not been reported and transhipment as 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(EventReportedPage(eventIndex), false)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.IncidentInformationController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to have seals changed Page when the event has been reported and Transhipment as 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to transhipment type page when Transhipment is 'Yes'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(IsTranshipmentPage(eventIndex), true)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.TranshipmentTypeController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to Session Expired when we cannot tell if the event has been reported or if Transhipment is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventReportedPage(eventIndex))
                .removeValue(IsTranshipmentPage(eventIndex))

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, updatedAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }

      "must go from Incident Information to have seals changed page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(IncidentInformationPage(eventIndex), mode, answers)
              .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
        }
      }

      "must go from Confirm Remove Event Page" - {

        "to Add Event Page when user selects 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(IncidentOnRoutePage, true)
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "TestPlace")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(ConfirmRemoveEventPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.AddEventController.onPageLoad(answers.movementReferenceNumber, mode))
          }
        }

        "to Add Event Page when user selects 'Yes' and events still exist" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(IncidentOnRoutePage, true)
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "TestPlace")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(ConfirmRemoveEventPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.AddEventController.onPageLoad(answers.movementReferenceNumber, mode))
          }
        }

        "to Incident on Route Page when user selects 'Yes' and no event exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val withoutEvents = answers.removeValue(EventsQuery)
              navigator
                .nextPage(ConfirmRemoveEventPage(eventIndex), mode, withoutEvents)
                .mustBe(routes.IncidentOnRouteController.onPageLoad(answers.movementReferenceNumber, mode))
          }
        }
      }

      "must go from Add Event Page" - {
        "when user selects 'Yes' to" - {

          "Event Country Page with index 0 when there are no events" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val withoutEvents = answers.removeValue(EventsQuery)

                val updatedAnswers = withoutEvents.setValue(AddEventPage, true)

                navigator
                  .nextPage(AddEventPage, mode, updatedAnswers)
                  .mustBe(eventRoutes.EventCountryController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
            }
          }

          "Event Country Page with index 1 when there is 1 event" in {
            forAll(arbitrary[EnRouteEvent], stringsWithMaxLength(350)) {
              case (EnRouteEvent(place, countryCode, _, _, _), information) =>
                val updatedAnswers = emptyUserAnswers
                  .setValue(IncidentOnRoutePage, true)
                  .setValue(EventCountryPage(eventIndex), countryCode)
                  .setValue(EventPlacePage(eventIndex), place)
                  .setValue(EventReportedPage(eventIndex), false)
                  .setValue(IsTranshipmentPage(eventIndex), false)
                  .setValue(IncidentInformationPage(eventIndex), information)
                  .setValue(AddEventPage, true)

                navigator
                  .nextPage(AddEventPage, mode, updatedAnswers)
                  .mustBe(eventRoutes.EventCountryController.onPageLoad(emptyUserAnswers.movementReferenceNumber, Index(1), mode))
            }
          }
        }

        "to check your answers page when user selects option 'No' on add event page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(AddEventPage, false)

              navigator
                .nextPage(AddEventPage, mode, updatedAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
          }
        }
      }
    }

    "in Check mode" - {

      val mode = CheckMode

      "must go from a page that doesn't exist in the edit route map  to Check Your Answers" in {
        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(UnknownPage, mode, answers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
        }
      }

      Seq(
        EventCountryPage(eventIndex),
        EventPlacePage(eventIndex),
        IncidentInformationPage(eventIndex)
      ) foreach {
        page =>
          s"must go from $page pages to check event answers" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(page, mode, answers)
                  .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
            }
          }
      }

      "must go from EventReportedPage pages" - {
        "to check event answers when event reported is true" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers.setValue(EventReportedPage(eventIndex), true)

              navigator
                .nextPage(EventReportedPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
          }
        }

        "to check event answers when event reported is false and transhipment is true" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(EventReportedPage(eventIndex), false)
                .setValue(IsTranshipmentPage(eventIndex), true)

              navigator
                .nextPage(EventReportedPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
          }
        }

        "to incident information when event reported is false and is not a transhipment" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(EventReportedPage(eventIndex), false)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(EventReportedPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.IncidentInformationController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
          }
        }
      }

      "must go from IsTranshipmentPage" - {

        "to TranshipmentTypePage when true and they have not answered TranshipmentType" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(IsTranshipmentPage(eventIndex), true)
                .removeValue(TranshipmentTypePage(eventIndex))

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(transhipmentRoutes.TranshipmentTypeController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to Check Event Answers when true and they have answered TranshipmentType and is Vehicle type" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentVehicle)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
          }
        }

        "to Check Event Answers when true and they have answered TranshipmentType and is Container or Both type and has a container" in {
          forAll(arbitrary[UserAnswers], Gen.oneOf(DifferentContainer, DifferentContainerAndVehicle), arbitrary[ContainerDomain]) {
            (answers, transhipmentType, container) =>
              val ua = answers
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), transhipmentType)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
          }
        }

        "to TranshipmentType when true and they have answered TranshipmentType and is Container or Both type there are no containers" in {
          forAll(arbitrary[UserAnswers], Gen.oneOf(DifferentContainer, DifferentContainerAndVehicle)) {
            (answers, transhipmentType) =>
              val ua = answers
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), transhipmentType)
                .removeValue(ContainersQuery(eventIndex))

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(transhipmentRoutes.TranshipmentTypeController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to Check Event Answers when false and ReportedEvent is true" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
          }
        }

        "to incident Information when false and ReportedEvent is false and they have not answered IncidentInformation" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(EventReportedPage(eventIndex), false)
                .setValue(IsTranshipmentPage(eventIndex), false)
                .removeValue(IncidentInformationPage(eventIndex))

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.IncidentInformationController.onPageLoad(ua.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to Check Event Answers when false, ReportedEvent is false and IncidentInformation has been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (answers, incidentInformationAnswer) =>
              val ua = answers
                .setValue(EventReportedPage(eventIndex), false)
                .setValue(IsTranshipmentPage(eventIndex), false)
                .setValue(IncidentInformationPage(eventIndex), incidentInformationAnswer)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), mode, ua)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(ua.movementReferenceNumber, eventIndex))
          }
        }
      }
    }
  }
}
