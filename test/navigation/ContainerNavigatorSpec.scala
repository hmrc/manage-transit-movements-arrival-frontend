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
import models.reference.CountryCode
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.Page
import pages.events.transhipments._
import pages.events.{EventCountryPage, EventPlacePage, EventReportedPage, IsTranshipmentPage}
import queries.EventsQuery

class ContainerNavigatorSpec extends SpecBase with Generators {

  private val navigator: Navigator = new ContainerNavigator()

  private val country: CountryCode = CountryCode("GB")

  "Container Navigator" - {

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
                .nextPage(ConfirmRemoveContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(ConfirmRemoveContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.IsTranshipmentController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.TransportIdentityController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, containerIndex, mode))
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
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, containerIndex, mode))
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
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }

      "must go from transport identity to Transport Nationality page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TransportIdentityPage(eventIndex), mode, answers)
              .mustBe(transhipmentRoutes.TransportNationalityController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
        }
      }

      "must go from transport nationality to have seals changed page" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(TransportNationalityPage(eventIndex), mode, answers)
              .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
        }
      }

      "must go from container number page to 'Add another container'" in {
        forAll(arbitrary[UserAnswers]) {
          answers =>
            navigator
              .nextPage(ContainerNumberPage(eventIndex, containerIndex), mode, answers)
              .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.TransportIdentityController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to 'Container number' with index 0 when the option is 'Yes' and there are no previous containers" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(EventsQuery)
                .setValue(AddContainerPage(eventIndex), true)

              navigator
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(answers.movementReferenceNumber, eventIndex, containerIndex, mode))
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
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(answers.movementReferenceNumber, eventIndex, nextIndex, mode))
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

      "must go from TranshipmentTypePage" - {

        "to ContainerNumberPage when 'A different container' is selected and ContainerNumber has not been answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
                .removeValue(ContainerNumberPage(eventIndex, containerIndex))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(
                  transhipmentRoutes.ContainerNumberController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex, containerIndex, mode)
                )
          }
        }

        "to CheckEventAnswers when 'A different container' is selected and ContainerNumber has been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
            (answers, container) =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex))
          }
        }

        "to TransportIdentityPage when 'A different vehicle' is selected and TransportIdentity has not been answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentVehicle)
                .removeValue(TransportIdentityPage(eventIndex))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(transhipmentRoutes.TransportIdentityController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to CheckEventAnswers when 'A different vehicle' is selected and TransportIdentity has been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (answers, transportIdentity) =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentVehicle)
                .setValue(TransportIdentityPage(eventIndex), transportIdentity)

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex))
          }
        }

        "to ContainerNumberPage when 'Both' is selected and ContainerNumber has not been answered" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .removeValue(ContainerNumberPage(eventIndex, containerIndex))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(
                  transhipmentRoutes.ContainerNumberController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex, containerIndex, mode)
                )
          }
        }

        "to CheckEventAnswers when 'Both' is selected and ContainerNumber and vehicle identity and nationality questions have been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain], arbitrary[String], arbitrary[CountryCode]) {
            (answers, container, transportIdentity, transportNationality) =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)
                .setValue(TransportIdentityPage(eventIndex), transportIdentity)
                .setValue(TransportNationalityPage(eventIndex), transportNationality)

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex))
          }
        }

        "to addContainerPage when 'Both' is selected and ContainerNumber has been answered but Transport Identity and Nationality has not been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
            (answers, container) =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)
                .removeValue(TransportIdentityPage(eventIndex))
                .removeValue(TransportNationalityPage(eventIndex))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to addContainerPage when 'Both' is selected and ContainerNumber has been answered but Transport Nationality has not been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
            (answers, container) =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)
                .removeValue(TransportNationalityPage(eventIndex))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to addContainerPage when 'Both' is selected and ContainerNumber has been answered but Transport Identity has not been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
            (answers, container) =>
              val updatedUserAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)
                .removeValue(TransportIdentityPage(eventIndex))

              navigator
                .nextPage(TranshipmentTypePage(eventIndex), mode, updatedUserAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedUserAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }

      "must go from ContainerNumberPage" - {

        "to AddContainer" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(ContainerNumberPage(eventIndex, containerIndex), mode, answers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }

      "must go from TransportIdentityPage" - {

        "to TransportNationalityPage when TransportNationality has not been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (answers, transportIdentity) =>
              val updatedUserAnswers = answers
                .setValue(TransportIdentityPage(eventIndex), transportIdentity)
                .removeValue(TransportNationalityPage(eventIndex))

              navigator
                .nextPage(TransportIdentityPage(eventIndex), mode, updatedUserAnswers)
                .mustBe(transhipmentRoutes.TransportNationalityController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to CheckEventAnswersPage when TransportNationality has been answered" in {
          forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[CountryCode]) {
            (answers, transportIdentity, transportNationality) =>
              val updatedUserAnswers = answers
                .setValue(TransportIdentityPage(eventIndex), transportIdentity)
                .setValue(TransportNationalityPage(eventIndex), transportNationality)

              navigator
                .nextPage(TransportIdentityPage(eventIndex), mode, updatedUserAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }
      }

      "must go from TransportNationality" - {

        "to CheckEventAnswers" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(TransportNationalityPage(eventIndex), mode, answers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }
      }

      "must go from AddContainerPage" - {
        "to CheckEventAnswers when false and the TranshipmentTypePage is 'A different container'" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
                .setValue(AddContainerPage(eventIndex), false)

              navigator
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex))
          }
        }

        "to TransportIdentityPage when false, TranshipmentTypePage is 'Both' and TransportIdentity has not been answered" in {

          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(AddContainerPage(eventIndex), false)
                .removeValue(TransportIdentityPage(eventIndex))

              navigator
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.TransportIdentityController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "to CheckEventAnswers when false, TranshipmentTypePage is 'Both' and TransportIdentity has been answered" in {

          forAll(arbitrary[UserAnswers], arbitrary[String]) {
            (answers, transportIdentity) =>
              val updatedAnswers = answers
                .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
                .setValue(AddContainerPage(eventIndex), false)
                .setValue(TransportIdentityPage(eventIndex), transportIdentity)

              navigator
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex))
          }
        }

        "to ContainerNumber page in when true with the index increased" in {
          val nextIndex = Index(containerIndex.position + 1)
          forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
            (answers, container) =>
              val updatedAnswers = answers
                .setValue(ContainerNumberPage(eventIndex, containerIndex), container)
                .setValue(AddContainerPage(eventIndex), true)

              navigator
                .nextPage(AddContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.ContainerNumberController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, nextIndex, mode))
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
                .nextPage(ConfirmRemoveContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(transhipmentRoutes.AddContainerController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
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
                .nextPage(ConfirmRemoveContainerPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.IsTranshipmentController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }
    }
  }
}
