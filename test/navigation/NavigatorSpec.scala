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
import generators.{Generators, MessagesModelGenerators}
import models.GoodsLocation.{AuthorisedConsigneesLocation, BorderForceOffice}
import models.TranshipmentType.{DifferentContainer, DifferentContainerAndVehicle, DifferentVehicle}
import models._
import models.domain.{ContainerDomain, SealDomain}
import models.messages.EnRouteEvent
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.events._
import pages.events.seals._
import pages.events.transhipments._
import queries.EventsQuery

class NavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with MessagesModelGenerators {

  val navigator: Navigator = new Navigator()

  val country: CountryCode = CountryCode("GB")

  "Navigator" - {

    "in Normal mode" - {

      "must go from a page that doesn't exist in the route map to Index" in {

        case object UnknownPage extends Page

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(UnknownPage, NormalMode, answers)
              .mustBe(routes.MovementReferenceNumberController.onPageLoad())
        }
      }

      "must go from movement reference number to 'Good location' page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(MovementReferenceNumberPage, NormalMode, answers)
              .mustBe(routes.GoodsLocationController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'goods location' to  'customs approved location' when user chooses 'Border Force Office' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.setValue(GoodsLocationPage, GoodsLocation.BorderForceOffice)

            navigator
              .nextPage(GoodsLocationPage, NormalMode, updatedAnswers)
              .mustBe(routes.CustomsSubPlaceController.onPageLoad(updatedAnswers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'authorisedLocationCode page to consigneeName page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(AuthorisedLocationPage, NormalMode, answers)
              .mustBe(routes.ConsigneeNameController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'consigneeName page to eoriConfirmation page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ConsigneeNamePage, NormalMode, answers)
              .mustBe(routes.ConsigneeEoriNumberController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'eoriNumber' page to consigneeAddress page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ConsigneeEoriNumberPage, NormalMode, answers)
              .mustBe(routes.ConsigneeAddressController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'ConsigneeAddress' page to 'supervising customs office' page when simplified journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ConsigneeAddressPage, NormalMode, answers)
              .mustBe(routes.CustomsOfficeSimplifiedController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'Presentation office'" - {
        "to 'EventOnRoute' in simplified journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(GoodsLocationPage, AuthorisedConsigneesLocation)
                .removeValue(IncidentOnRoutePage)
              navigator
                .nextPage(SimplifiedCustomsOfficePage, NormalMode, ua)
                .mustBe(routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, NormalMode))
          }
        }

        "to 'TraderName' in normal journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(GoodsLocationPage, BorderForceOffice)
                .removeValue(TraderNamePage)
              navigator
                .nextPage(CustomsOfficePage, NormalMode, ua)
                .mustBe(routes.TraderNameController.onPageLoad(ua.movementReferenceNumber, NormalMode))
          }
        }

        "to 'SessionExpired' when no answers are available" in {
          navigator
            .nextPage(CustomsOfficePage, NormalMode, emptyUserAnswers)
            .mustBe(routes.SessionExpiredController.onPageLoad())
        }
      }

      "must go from 'customs approved location' to  'presentation office'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CustomsSubPlacePage, NormalMode, answers)
              .mustBe(routes.CustomsOfficeController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'traders name' to 'traders eori'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TraderNamePage, NormalMode, answers)
              .mustBe(routes.TraderEoriController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'traders address' to 'IsTraderAddressPlaceOfNotificationController'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TraderAddressPage, NormalMode, answers)
              .mustBe(routes.IsTraderAddressPlaceOfNotificationController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'traders eori' to 'traders address'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TraderEoriPage, NormalMode, answers)
              .mustBe(routes.TraderAddressController.onPageLoad(answers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'IsTraderAddressPlaceOfNotificationPage'" - {
        "to 'IncidentOnRoutePage' when answer is 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.setValue(IsTraderAddressPlaceOfNotificationPage, true)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, NormalMode, updatedUserAnswers)
                .mustBe(routes.IncidentOnRouteController.onPageLoad(updatedUserAnswers.movementReferenceNumber, NormalMode))
          }
        }

        "to 'IncidentOnRoutePage' when answer is 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.setValue(IsTraderAddressPlaceOfNotificationPage, false)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, NormalMode, updatedUserAnswers)
                .mustBe(routes.PlaceOfNotificationController.onPageLoad(updatedUserAnswers.movementReferenceNumber, NormalMode))
          }
        }
      }

      "go from 'Place of Notification' to 'IncidentOnRoute'" in {
        forAll(arbitrary[UserAnswers], stringsWithMaxLength(35)) {
          (answers, placeOfNotification) =>
            val updatedUserAnswers = answers.setValue(PlaceOfNotificationPage, placeOfNotification)

            navigator
              .nextPage(PlaceOfNotificationPage, NormalMode, updatedUserAnswers)
              .mustBe(routes.IncidentOnRouteController.onPageLoad(updatedUserAnswers.movementReferenceNumber, NormalMode))
        }
      }

      "must go from 'incident on route'" - {

        "to 'check your answers' when the user answers no" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(IncidentOnRoutePage, false)

              navigator
                .nextPage(IncidentOnRoutePage, NormalMode, updatedAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
          }
        }

        "must go to AddEvent if existing events" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers =
                answers
                  .setValue(IncidentOnRoutePage, true)
                  .setValue(EventCountryPage(eventIndex), country)
                  .setValue(EventPlacePage(eventIndex), "TestPlace")
                  .setValue(EventReportedPage(eventIndex), true)
                  .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(IncidentOnRoutePage, NormalMode, updatedAnswers)
                .mustBe(eventRoutes.AddEventController.onPageLoad(answers.movementReferenceNumber, NormalMode))
          }
        }

        "must go to EventCountry if no events" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers =
                answers
                  .setValue(IncidentOnRoutePage, true)
                  .removeValue(EventsQuery)

              navigator
                .nextPage(IncidentOnRoutePage, NormalMode, updatedAnswers)
                .mustBe(eventRoutes.EventCountryController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to Session Expired when we cannot tell if an event happened on route" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.removeValue(IncidentOnRoutePage)

              navigator
                .nextPage(IncidentOnRoutePage, NormalMode, updatedAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }

      "must go from Event Country to Event Place" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EventCountryPage(eventIndex), NormalMode, answers)
              .mustBe(eventRoutes.EventPlaceController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
        }
      }

      "must go from Event Place to Event Reported" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EventPlacePage(eventIndex), NormalMode, answers)
              .mustBe(eventRoutes.EventReportedController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
        }
      }

      "must go from Event Reported to Is Transhipment" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(EventReportedPage(eventIndex), NormalMode, answers)
              .mustBe(eventRoutes.IsTranshipmentController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
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
                .nextPage(IsTranshipmentPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(eventRoutes.IncidentInformationController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to have seals changed Page when the event has been reported and Transhipment as 'No'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), false)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to transhipment type page when Transhipment is 'Yes'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(IsTranshipmentPage(eventIndex), true)

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.TranshipmentTypeController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to Session Expired when we cannot tell if the event has been reported or if Transhipment is selected" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventReportedPage(eventIndex))
                .removeValue(IsTranshipmentPage(eventIndex))

              navigator
                .nextPage(IsTranshipmentPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }

      "must go from Confirm remove container page" - {

        "to Add container page when containers exist" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "place name")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), ContainerDomain("1"))

              navigator
                .nextPage(ConfirmRemoveContainerPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to isTranshipment page when no containers exist" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "place name")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)

              navigator
                .nextPage(ConfirmRemoveContainerPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(eventRoutes.IsTranshipmentController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }
      }

      "must go from Transhipment type" - {

        "to Transport Identity when option is 'a different vehicle' " in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentVehicle)

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.TransportIdentityController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to ContainerNumber when option is 'a different container' and there are no containers" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "place name")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, containerIndex, NormalMode))
          }
        }

        "to Add Container when option is 'a different container' and there is one container" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "place name")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), ContainerDomain("1"))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to ContainerNumber when option is 'both' and there is a no container " in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "place name")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, containerIndex, NormalMode))
          }
        }

        "to Add Container when option is 'both' and there is a single container " in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(EventCountryPage(eventIndex), country)
                .setValue(EventPlacePage(eventIndex), "place name")
                .setValue(EventReportedPage(eventIndex), true)
                .setValue(IsTranshipmentPage(eventIndex), true)
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), ContainerDomain("number1"))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }
      }

      "must go from transport identity to Transport Nationality page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TransportIdentityPage(eventIndex), NormalMode, answers)
              .mustBe(transhipmentRoutes.TransportNationalityController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
        }
      }

      "must go from transport nationality to have seals changed page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TransportNationalityPage(eventIndex), NormalMode, answers)
              .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
        }
      }

      "must go from Incident Information to have seals changed page" in {

        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(IncidentInformationPage(eventIndex), NormalMode, answers)
              .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
        }
      }

      "must go from container number page to 'Add another container'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContainerNumberPage(eventIndex, containerIndex), NormalMode, answers)
              .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
        }
      }

      "must go from 'Add another container'" - {

        "to 'transport identity' when the option is 'No' and transhipment type is both" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(AddContainerPage(eventIndex), false)

              navigator
                .nextPage(AddContainerPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.TransportIdentityController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to have seals changed page when the option is 'No' and transhipment type is not both" in {

          val transhipmentType: Gen[WithName with TranshipmentType] = Gen.oneOf(Seq(DifferentContainer, DifferentVehicle))
          forAll(arbitrary[UserAnswers], transhipmentType) {
            (answers, transhipment) =>
              val updatedAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), transhipment)
                .setValue(AddContainerPage(eventIndex), false)

              navigator
                .nextPage(AddContainerPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "to 'Container number' with index 0 when the option is 'Yes' and there are no previous containers" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(AddContainerPage(eventIndex), true)

              navigator
                .nextPage(AddContainerPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(answers.movementReferenceNumber, eventIndex, containerIndex, NormalMode))
          }
        }

        "to 'Container number' with index 1 when the option is 'Yes' and there is 1 previous containers" in {
          val nextIndex = Index(containerIndex.position + 1)

          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
            case (answers, container) =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)
                .setValue(AddContainerPage(eventIndex), true)

              navigator
                .nextPage(AddContainerPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(answers.movementReferenceNumber, eventIndex, nextIndex, NormalMode))
          }
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
                .nextPage(ConfirmRemoveEventPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(eventRoutes.AddEventController.onPageLoad(answers.movementReferenceNumber, NormalMode))
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
                .nextPage(ConfirmRemoveEventPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(eventRoutes.AddEventController.onPageLoad(answers.movementReferenceNumber, NormalMode))
          }
        }

        "to Incident on Route Page when user selects 'Yes' and no event exists" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val withoutEvents = answers.removeValue(EventsQuery)
              navigator
                .nextPage(ConfirmRemoveEventPage(eventIndex), NormalMode, withoutEvents)
                .mustBe(routes.IncidentOnRouteController.onPageLoad(answers.movementReferenceNumber, NormalMode))
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
                  .nextPage(AddEventPage, NormalMode, updatedAnswers)
                  .mustBe(eventRoutes.EventCountryController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
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
                  .nextPage(AddEventPage, NormalMode, updatedAnswers)
                  .mustBe(eventRoutes.EventCountryController.onPageLoad(emptyUserAnswers.movementReferenceNumber, Index(1), NormalMode))
            }
          }
        }

        "to check your answers page when user selects option 'No' on add event page" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(AddEventPage, false)

              navigator
                .nextPage(AddEventPage, NormalMode, updatedAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
          }
        }
      }

      "must go from Have seals changed page" - {

        "to check event answers page when user selects No" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(HaveSealsChangedPage(eventIndex), false)

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }

        "to seal identity page when user selects Yes" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(HaveSealsChangedPage(eventIndex), true)
                .removeValue(SealIdentityPage(eventIndex, sealIndex))

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.SealIdentityController.onPageLoad(answers.movementReferenceNumber, eventIndex, sealIndex, NormalMode))
          }
        }

        "to 'add seal page'" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (answers, seal) =>
              val updatedAnswers = answers
                .setValue(SealIdentityPage(eventIndex, sealIndex), seal)
                .setValue(HaveSealsChangedPage(eventIndex), true)

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }
      }

      "must go from seals identity page" - {

        "to check event answers page" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (answers, seal) =>
              val updatedAnswers = answers.setValue(SealIdentityPage(eventIndex, sealIndex), seal)

              navigator
                .nextPage(SealIdentityPage(eventIndex, sealIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }
      }

      "must go from add seal page" - {

        "to check event details page when answer is no" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(AddSealPage(eventIndex), false)

              navigator
                .nextPage(AddSealPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }

        "to seal identity page when answer is Yes" in {
          val nextIndex = Index(sealIndex.position + 1)
          val updatedAnswers = emptyUserAnswers
            .setValue(AddSealPage(eventIndex), true)
            .setValue(SealIdentityPage(eventIndex, sealIndex), SealDomain("seal1"))

          navigator
            .nextPage(AddSealPage(eventIndex), NormalMode, updatedAnswers)
            .mustBe(sealRoutes.SealIdentityController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, nextIndex, NormalMode))
        }
      }

      "go from remove seals page" - {
        "add seals page when 'Yes' is selected and there are still seals" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            case (userAnswers, seal) =>
              val updatedAnswers = userAnswers
                .setValue(SealIdentityPage(eventIndex, sealIndex), seal)

              navigator
                .nextPage(ConfirmRemoveSealPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }

        "have seals changed page when 'Yes' is selected and all seals are removed" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedAnswers = userAnswers
                .removeValue(EventsQuery)

              navigator
                .nextPage(ConfirmRemoveSealPage(eventIndex), NormalMode, updatedAnswers)
                .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }
      }
    }

    "nextRejectionPage" - {

      "must go from UpdateRejectionMovementReferenceNumber page to CheckYourAnswers page" in {
        navigator
          .nextPage(UpdateRejectedMRNPage, NormalMode, emptyUserAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad(mrn))
      }
    }
  }
}
