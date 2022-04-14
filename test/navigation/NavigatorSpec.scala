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
import controllers.events.{routes => eventRoutes}
import controllers.routes
import generators.Generators
import models.GoodsLocation.{AuthorisedConsigneesLocation, BorderForceOffice}
import models._
import models.reference.{CountryCode, CustomsOffice}
import org.scalacheck.Arbitrary.arbitrary
import pages._
import pages.events.{EventCountryPage, EventPlacePage, EventReportedPage, IsTranshipmentPage}
import queries.EventsQuery

class NavigatorSpec extends SpecBase with Generators {

  private val navigator: Navigator = new Navigator()

  private val country: CountryCode = CountryCode("GB")

  "Navigator" - {

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

      "must go from movement reference number to 'Good location' page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(MovementReferenceNumberPage, mode, answers)
              .mustBe(routes.GoodsLocationController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'goods location' to  'customs approved location' when user chooses 'Border Force Office' " in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            val updatedAnswers = answers.setValue(GoodsLocationPage, GoodsLocation.BorderForceOffice)

            navigator
              .nextPage(GoodsLocationPage, mode, updatedAnswers)
              .mustBe(routes.CustomsSubPlaceController.onPageLoad(updatedAnswers.movementReferenceNumber, mode))
        }
      }

      "must go from 'authorisedLocationCode page to consigneeName page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(AuthorisedLocationPage, mode, answers)
              .mustBe(routes.ConsigneeNameController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'consigneeName page to eoriConfirmation page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ConsigneeNamePage, mode, answers)
              .mustBe(routes.ConsigneeEoriNumberController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'eoriNumber' page to consigneeAddress page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ConsigneeEoriNumberPage, mode, answers)
              .mustBe(routes.ConsigneeAddressController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'ConsigneeAddress' page to 'supervising customs office' page when simplified journey" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ConsigneeAddressPage, mode, answers)
              .mustBe(routes.CustomsOfficeSimplifiedController.onPageLoad(answers.movementReferenceNumber, mode))
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
                .nextPage(SimplifiedCustomsOfficePage, mode, ua)
                .mustBe(routes.IncidentOnRouteController.onPageLoad(ua.movementReferenceNumber, mode))
          }
        }

        "to 'TraderName' in normal journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val ua = answers
                .setValue(GoodsLocationPage, BorderForceOffice)
                .removeValue(TraderNamePage)
              navigator
                .nextPage(CustomsOfficePage, mode, ua)
                .mustBe(routes.TraderNameController.onPageLoad(ua.movementReferenceNumber, mode))
          }
        }

        "to 'SessionExpired' when no answers are available" in {
          navigator
            .nextPage(CustomsOfficePage, mode, emptyUserAnswers)
            .mustBe(routes.SessionExpiredController.onPageLoad())
        }
      }

      "must go from 'customs approved location' to  'presentation office'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(CustomsSubPlacePage, mode, answers)
              .mustBe(routes.CustomsOfficeController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'traders name' to 'traders eori'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TraderNamePage, mode, answers)
              .mustBe(routes.TraderEoriController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'traders address' to 'IsTraderAddressPlaceOfNotificationController'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TraderAddressPage, mode, answers)
              .mustBe(routes.IsTraderAddressPlaceOfNotificationController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'traders eori' to 'traders address'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TraderEoriPage, mode, answers)
              .mustBe(routes.TraderAddressController.onPageLoad(answers.movementReferenceNumber, mode))
        }
      }

      "must go from 'IsTraderAddressPlaceOfNotificationPage'" - {
        "to 'IncidentOnRoutePage' when answer is 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.setValue(IsTraderAddressPlaceOfNotificationPage, true)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedUserAnswers)
                .mustBe(routes.IncidentOnRouteController.onPageLoad(updatedUserAnswers.movementReferenceNumber, mode))
          }
        }

        "to 'IncidentOnRoutePage' when answer is 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.setValue(IsTraderAddressPlaceOfNotificationPage, false)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedUserAnswers)
                .mustBe(routes.PlaceOfNotificationController.onPageLoad(updatedUserAnswers.movementReferenceNumber, mode))
          }
        }
      }

      "go from 'Place of Notification' to 'IncidentOnRoute'" in {
        forAll(arbitrary[UserAnswers], stringsWithMaxLength(35)) {
          (answers, placeOfNotification) =>
            val updatedUserAnswers = answers.setValue(PlaceOfNotificationPage, placeOfNotification)

            navigator
              .nextPage(PlaceOfNotificationPage, mode, updatedUserAnswers)
              .mustBe(routes.IncidentOnRouteController.onPageLoad(updatedUserAnswers.movementReferenceNumber, mode))
        }
      }

      "must go from 'incident on route'" - {

        "to 'check your answers' when the user answers no" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(IncidentOnRoutePage, false)

              navigator
                .nextPage(IncidentOnRoutePage, mode, updatedAnswers)
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
                .nextPage(IncidentOnRoutePage, mode, updatedAnswers)
                .mustBe(eventRoutes.AddEventController.onPageLoad(answers.movementReferenceNumber, mode))
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
                .nextPage(IncidentOnRoutePage, mode, updatedAnswers)
                .mustBe(eventRoutes.EventCountryController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to Session Expired when we cannot tell if an event happened on route" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.removeValue(IncidentOnRoutePage)

              navigator
                .nextPage(IncidentOnRoutePage, mode, updatedAnswers)
                .mustBe(routes.SessionExpiredController.onPageLoad())
          }
        }
      }

      "must go from UpdateRejectionMovementReferenceNumber page to CheckYourAnswers page" in {
        navigator
          .nextPage(UpdateRejectedMRNPage, mode, emptyUserAnswers)
          .mustBe(routes.CheckYourAnswersController.onPageLoad(mrn))
      }
    }

    "in Check mode" - {

      val mode = CheckMode

      "must go from Goods Location" - {
        "to Check Your Answers" - {
          "when the user answers Border Force Office and they have already answered Customs Sub Place" in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, subPlace) =>
                val updatedAnswers =
                  answers
                    .setValue(GoodsLocationPage, GoodsLocation.BorderForceOffice)
                    .setValue(CustomsSubPlacePage, subPlace)

                navigator
                  .nextPage(GoodsLocationPage, mode, updatedAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }
        }

        "to Customs Sub Place" - {
          "when the user answers Border Force Office and had not answered Customs Sub Place" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers =
                  answers
                    .setValue(GoodsLocationPage, GoodsLocation.BorderForceOffice)
                    .removeValue(CustomsSubPlacePage)

                navigator
                  .nextPage(GoodsLocationPage, mode, updatedAnswers)
                  .mustBe(routes.CustomsSubPlaceController.onPageLoad(answers.movementReferenceNumber, mode))
            }
          }
        }
      }

      "must go from 'CustomsSubPlaceController' to " - {
        "'CustomsOfficeController' when no previous answer for 'CustomsOffice'" in {
          forAll(arbitrary[UserAnswers], nonEmptyString) {
            (answers, customsSubPlace) =>
              val updatedAnswers =
                answers
                  .setValue(CustomsSubPlacePage, customsSubPlace)

              navigator
                .nextPage(CustomsSubPlacePage, mode, updatedAnswers)
                .mustBe(routes.CustomsOfficeController.onPageLoad(answers.movementReferenceNumber, mode))
          }
        }

        "'CheckYourAnswersPage' when already answer for 'CustomsOffice'" in {
          forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[CustomsOffice]) {
            (answers, customsSubPlace, customsOffice) =>
              val updatedAnswers =
                answers
                  .setValue(CustomsSubPlacePage, customsSubPlace)
                  .setValue(CustomsOfficePage, customsOffice)

              navigator
                .nextPage(CustomsSubPlacePage, mode, updatedAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
          }
        }

        "CheckYourAnswersPage when trader at destination name has already been answered " in {
          forAll(arbitrary[UserAnswers], arbitrary[CustomsOffice], arbitrary[String]) {
            (answers, customsOffice, traderName) =>
              val updatedAnswers =
                answers
                  .setValue(CustomsOfficePage, customsOffice)
                  .setValue(TraderNamePage, traderName)

              navigator
                .nextPage(CustomsSubPlacePage, mode, updatedAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
          }
        }
      }

      "must go from OfficeOfPresentation to" - {
        "trader name when trader name has not previously been answered on Normal Route" in {
          forAll(arbitrary[UserAnswers], arbitrary[CustomsOffice]) {
            (answers, customsOffice) =>
              val updatedAnswers =
                answers
                  .setValue(GoodsLocationPage, BorderForceOffice)
                  .setValue(CustomsOfficePage, customsOffice)
                  .removeValue(TraderNamePage)

              navigator
                .nextPage(CustomsOfficePage, mode, updatedAnswers)
                .mustBe(routes.TraderNameController.onPageLoad(answers.movementReferenceNumber, mode))
          }
        }

        "from trader name page to" - {
          "CheckYourAnswersPage when trader eori has previously been answered " in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, traderEori) =>
                val updatedAnswers =
                  answers
                    .setValue(TraderNamePage, traderName)
                    .setValue(TraderEoriPage, traderEori)

                navigator
                  .nextPage(TraderNamePage, mode, updatedAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }

          "TraderEoriPage when trader eori has not previously been answered " in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers =
                  answers.removeValue(TraderEoriPage)

                navigator
                  .nextPage(TraderNamePage, mode, updatedAnswers)
                  .mustBe(routes.TraderEoriController.onPageLoad(answers.movementReferenceNumber, mode))
            }
          }
        }

        "from trader eori page to" - {
          "CheckYourAnswersPage when trader address has previously been answered " in {
            forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[Address]) {
              (answers, traderEori, traderAddress) =>
                val updatedAnswers =
                  answers
                    .setValue(TraderEoriPage, traderEori)
                    .setValue(TraderAddressPage, traderAddress)

                navigator
                  .nextPage(TraderEoriPage, mode, updatedAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }

          "TraderAddressPage when trader address has not previously been answered " in {
            forAll(arbitrary[UserAnswers], arbitrary[String]) {
              (answers, traderEori) =>
                val updatedAnswers =
                  answers
                    .setValue(TraderEoriPage, traderEori)
                    .removeValue(TraderAddressPage)

                navigator
                  .nextPage(TraderEoriPage, mode, updatedAnswers)
                  .mustBe(routes.TraderAddressController.onPageLoad(answers.movementReferenceNumber, mode))
            }
          }
        }

        "must go from 'TraderAddressController' to " - {
          "'IsTraderAddressPlaceOfNotifcationController' when this is not answered" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .removeValue(IsTraderAddressPlaceOfNotificationPage)

                navigator
                  .nextPage(TraderAddressPage, mode, userAnswers)
                  .mustBe(routes.IsTraderAddressPlaceOfNotificationController.onPageLoad(userAnswers.movementReferenceNumber, mode))
            }
          }

          "'CheckYourAnswersController' when 'IsTraderAddressPlaceOfNotification' has been answered" in {
            forAll(arbitrary[UserAnswers], arbitrary[Boolean]) {
              (answers, isTraderAddressPlaceOfNotification) =>
                val userAnswers = answers
                  .setValue(IsTraderAddressPlaceOfNotificationPage, isTraderAddressPlaceOfNotification)

                navigator
                  .nextPage(TraderAddressPage, mode, userAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(userAnswers.movementReferenceNumber))
            }
          }
        }

        "must go from 'IsTraderAddressPlaceOfNotificationController' to " - {
          "'PlaceOfNotificationController' when the answer is false and it has not been answered" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .setValue(IsTraderAddressPlaceOfNotificationPage, false)
                  .removeValue(PlaceOfNotificationPage)

                navigator
                  .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, userAnswers)
                  .mustBe(routes.PlaceOfNotificationController.onPageLoad(userAnswers.movementReferenceNumber, mode))
            }
          }

          "'CheckYourAnswersController' when the answer is true" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val userAnswers = answers
                  .setValue(IsTraderAddressPlaceOfNotificationPage, true)

                navigator
                  .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, userAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }

          "'CheckYourAnswersController' when the answer is false when PlaceOfNotification has been answered" in {
            forAll(arbitrary[UserAnswers], nonEmptyString) {
              (answers, placeOfNotification) =>
                val userAnswers = answers
                  .setValue(IsTraderAddressPlaceOfNotificationPage, false)
                  .setValue(PlaceOfNotificationPage, placeOfNotification)

                navigator
                  .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, userAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }
        }

        "must go from 'PlaceOfNotification' to " - {
          "'CheckYourAnswersController'" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                navigator
                  .nextPage(PlaceOfNotificationPage, mode, answers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }
        }

        "must go from 'AuthorisedLocationController' to " - {
          "'CheckYourAnswersController' when an answer for 'ConsigneeName'" in {
            forAll(arbitrary[UserAnswers], nonEmptyString, nonEmptyString) {
              (answers, authorisedLocation, consigneeName) =>
                val updatedAnswers =
                  answers
                    .setValue(AuthorisedLocationPage, authorisedLocation)
                    .setValue(ConsigneeNamePage, consigneeName)

                navigator
                  .nextPage(AuthorisedLocationPage, mode, updatedAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }
        }

        "must go from 'ConsigneeNameController' to " - {
          "'Consignee eori number page no address is present'" in {
            forAll(arbitrary[UserAnswers], nonEmptyString) {
              (answers, consigneeName) =>
                val updatedAnswers =
                  answers
                    .setValue(ConsigneeNamePage, consigneeName)
                    .removeValue(ConsigneeAddressPage)

                navigator
                  .nextPage(ConsigneeNamePage, mode, updatedAnswers)
                  .mustBe(routes.ConsigneeEoriNumberController.onPageLoad(answers.movementReferenceNumber, mode))
            }
          }

          "'CYA page' when address is present" in {
            forAll(arbitrary[UserAnswers], nonEmptyString) {
              (answers, consigneeName) =>
                val updatedAnswers =
                  answers
                    .setValue(ConsigneeNamePage, consigneeName)
                    .setValue(ConsigneeAddressPage, traderAddress)

                navigator
                  .nextPage(ConsigneeNamePage, mode, updatedAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(answers.movementReferenceNumber))
            }
          }
        }

        "must go from 'ConsigneeEoriNumberController' to " - {
          "'Consignee Address page' when no address is present" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .removeValue(ConsigneeAddressPage)

                navigator
                  .nextPage(ConsigneeEoriNumberPage, mode, updatedAnswers)
                  .mustBe(routes.ConsigneeAddressController.onPageLoad(updatedAnswers.movementReferenceNumber, mode))
            }
          }

          "'CYA page' when  address is present" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers
                  .setValue(ConsigneeAddressPage, traderAddress)

                navigator
                  .nextPage(ConsigneeEoriNumberPage, mode, updatedAnswers)
                  .mustBe(routes.CheckYourAnswersController.onPageLoad(updatedAnswers.movementReferenceNumber))
            }
          }
        }

        "to Use Different Service" - {
          "when the user answers Authorised Consignee" in {
            forAll(arbitrary[UserAnswers]) {
              answers =>
                val updatedAnswers = answers.setValue(GoodsLocationPage, GoodsLocation.AuthorisedConsigneesLocation)

                navigator
                  .nextPage(GoodsLocationPage, mode, updatedAnswers)
                  .mustBe(routes.UseDifferentServiceController.onPageLoad(answers.movementReferenceNumber))
            }
          }
        }
      }

      "must go from 'IsTraderAddressPlaceOfNotificationPage'" - {
        "to 'Check Your Answers' when answer is 'No' and there is a 'Place of notification'" in {
          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (answers, placeOfNotification) =>
              val updatedUserAnswers = answers
                .setValue(IsTraderAddressPlaceOfNotificationPage, false)
                .setValue(PlaceOfNotificationPage, placeOfNotification)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedUserAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(updatedUserAnswers.movementReferenceNumber))
          }
        }

        "to 'Place of notification' when answer is 'No' and there is no existing 'Place of notification'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers
                .setValue(IsTraderAddressPlaceOfNotificationPage, false)
                .removeValue(PlaceOfNotificationPage)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedUserAnswers)
                .mustBe(routes.PlaceOfNotificationController.onPageLoad(updatedUserAnswers.movementReferenceNumber, mode))
          }
        }

        "to 'Check Your Answers' when answer is 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers.setValue(IsTraderAddressPlaceOfNotificationPage, true)

              navigator
                .nextPage(IsTraderAddressPlaceOfNotificationPage, mode, updatedUserAnswers)
                .mustBe(routes.CheckYourAnswersController.onPageLoad(updatedUserAnswers.movementReferenceNumber))
          }
        }
      }

      "go from 'Place of Notification' to CheckYourAnswer" in {
        import models.domain.NormalNotification.Constants.notificationPlaceLength

        forAll(arbitrary[UserAnswers], stringsWithMaxLength(notificationPlaceLength)) {
          case (answers, placeOfNotification) =>
            val updatedUserAnswers = answers.setValue(PlaceOfNotificationPage, placeOfNotification)

            navigator
              .nextPage(PlaceOfNotificationPage, mode, updatedUserAnswers)
              .mustBe(routes.CheckYourAnswersController.onPageLoad(updatedUserAnswers.movementReferenceNumber))
        }
      }

      "must go from incident on route page" - {

        "to event country page when user selects yes" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(IncidentOnRoutePage)
                .setValue(IncidentOnRoutePage, true)

              navigator
                .nextPage(IncidentOnRoutePage, mode, updatedAnswers)
                .mustBe(eventRoutes.EventCountryController.onPageLoad(answers.movementReferenceNumber, eventIndex, NormalMode))
          }
        }
      }
    }
  }
}
