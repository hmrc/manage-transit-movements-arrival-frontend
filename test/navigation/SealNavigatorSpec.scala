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
import controllers.events.{routes => eventRoutes}
import controllers.routes
import generators.Generators
import models._
import models.domain.SealDomain
import org.scalacheck.Arbitrary.arbitrary
import pages.Page
import pages.events.seals._
import queries.EventsQuery

class SealNavigatorSpec extends SpecBase with Generators {

  private val navigator: Navigator = new SealNavigator()

  "Seal Navigator" - {

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

      "must go from Have seals changed page" - {

        "to check event answers page when user selects No" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(HaveSealsChangedPage(eventIndex), false)

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), mode, updatedAnswers)
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
                .nextPage(HaveSealsChangedPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.SealIdentityController.onPageLoad(answers.movementReferenceNumber, eventIndex, sealIndex, mode))
          }
        }

        "to 'add seal page'" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (answers, seal) =>
              val updatedAnswers = answers
                .setValue(SealIdentityPage(eventIndex, sealIndex), seal)
                .setValue(HaveSealsChangedPage(eventIndex), true)

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }

      "must go from seals identity page" - {

        "to check event answers page" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (answers, seal) =>
              val updatedAnswers = answers.setValue(SealIdentityPage(eventIndex, sealIndex), seal)

              navigator
                .nextPage(SealIdentityPage(eventIndex, sealIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }

      "must go from add seal page" - {

        "to check event details page when answer is no" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(AddSealPage(eventIndex), false)

              navigator
                .nextPage(AddSealPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }

        "to seal identity page when answer is Yes" in {
          val nextIndex = Index(sealIndex.position + 1)
          val updatedAnswers = emptyUserAnswers
            .setValue(AddSealPage(eventIndex), true)
            .setValue(SealIdentityPage(eventIndex, sealIndex), SealDomain("seal1"))

          navigator
            .nextPage(AddSealPage(eventIndex), mode, updatedAnswers)
            .mustBe(sealRoutes.SealIdentityController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, nextIndex, mode))
        }
      }

      "go from remove seals page" - {
        "add seals page when 'Yes' is selected and there are still seals" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            case (userAnswers, seal) =>
              val updatedAnswers = userAnswers
                .setValue(SealIdentityPage(eventIndex, sealIndex), seal)

              navigator
                .nextPage(ConfirmRemoveSealPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "have seals changed page when 'Yes' is selected and all seals are removed" in {
          forAll(arbitrary[UserAnswers]) {
            userAnswers =>
              val updatedAnswers = userAnswers
                .removeValue(EventsQuery)

              navigator
                .nextPage(ConfirmRemoveSealPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.HaveSealsChangedController.onPageLoad(updatedAnswers.movementReferenceNumber, eventIndex, mode))
          }
        }
      }
    }

    "in Check mode" - {

      val mode = CheckMode

      "seals page" - {

        "must go from seals identity page to add seals page" in {
          forAll(arbitrary[UserAnswers], arbitrary[SealDomain]) {
            (answers, seal) =>
              val updatedAnswers = answers.setValue(SealIdentityPage(eventIndex, sealIndex), seal)

              navigator
                .nextPage(SealIdentityPage(eventIndex, sealIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.AddSealController.onPageLoad(answers.movementReferenceNumber, eventIndex, mode))
          }
        }

        "must go from have seals changed page to check event answers page when the answer is 'No'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers.setValue(HaveSealsChangedPage(eventIndex), false)

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }

        "must go from have seals changed page to seal identity page page when the answer is 'Yes'" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(HaveSealsChangedPage(eventIndex), true)
                .removeValue(SealIdentityPage(eventIndex, sealIndex))

              navigator
                .nextPage(HaveSealsChangedPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.SealIdentityController.onPageLoad(answers.movementReferenceNumber, eventIndex, sealIndex, mode))
          }
        }

        "go from addSealPage to sealIdentity when Yes is selected" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .removeValue(SealIdentityPage(eventIndex, sealIndex))
                .setValue(AddSealPage(eventIndex), true)

              navigator
                .nextPage(AddSealPage(eventIndex), mode, updatedAnswers)
                .mustBe(sealRoutes.SealIdentityController.onPageLoad(answers.movementReferenceNumber, eventIndex, sealIndex, mode))
          }
        }

        "go from addSealPage to checkEventAnswers when No is selected" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              val updatedAnswers = answers
                .setValue(AddSealPage(eventIndex), false)

              navigator
                .nextPage(AddSealPage(eventIndex), mode, updatedAnswers)
                .mustBe(eventRoutes.CheckEventAnswersController.onPageLoad(answers.movementReferenceNumber, eventIndex))
          }
        }
      }
    }
  }
}
