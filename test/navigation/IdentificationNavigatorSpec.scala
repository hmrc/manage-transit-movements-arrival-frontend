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
import controllers.identification.{routes => idRoutes}
import controllers.identification.authorisation.{routes => idAuthRoutes}
import generators.{Generators, UserAnswersGenerator}
import models._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages._
import pages.identification._
import pages.identification.authorisation._

class IdentificationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with UserAnswersGenerator {

  private val navigator = new IdentificationNavigator
  private val index     = Index(0)
  "Navigator" - {
    "must go from a page that doesn't exist in the route map" - {

      case object UnknownPage extends Page

      "when in normal mode" - {
        "to start of the departure journey" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, NormalMode, answers)
                .mustBe(idRoutes.MovementReferenceNumberController.onPageLoad())
          }
        }
      }

      "when in check mode" - {
        "to session expired" in {
          forAll(arbitrary[UserAnswers]) {
            answers =>
              navigator
                .nextPage(UnknownPage, CheckMode, answers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
          }
        }
      }
    }

    "when in NormalMode" - {

      val mode = NormalMode

      "when answers incomplete" - {

        "must go from Movement Reference Number page to Arrival Date page" in {
          navigator
            .nextPage(MovementReferenceNumberPage, mode, emptyUserAnswers)
            .mustBe(idRoutes.ArrivalDateController.onPageLoad(emptyUserAnswers.mrn, mode))
        }

        "must go from Arrival Date page to Is Simplified Page" in {
          navigator
            .nextPage(ArrivalDatePage, mode, emptyUserAnswers)
            .mustBe(idRoutes.IsSimplifiedProcedureController.onPageLoad(emptyUserAnswers.mrn, mode))
        }

        "must go from is Is Simplified Page" - {
          "when Yes selected" - {
            "to Authorisation Type Page" in {
              val userAnswers = emptyUserAnswers.setValue(IsSimplifiedProcedurePage, true)
              navigator
                .nextPage(IsSimplifiedProcedurePage, mode, userAnswers)
                .mustBe(idAuthRoutes.AuthorisationTypeController.onPageLoad(userAnswers.mrn, index, mode))
            }
          }

          "when No selected" - {
            "to Identification Number page" in {
              val userAnswers = emptyUserAnswers.setValue(IsSimplifiedProcedurePage, false)
              navigator
                .nextPage(IsSimplifiedProcedurePage, mode, userAnswers)
                .mustBe(idRoutes.IdentificationNumberController.onPageLoad(userAnswers.mrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(IsSimplifiedProcedurePage, mode, emptyUserAnswers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
            }
          }
        }

        "must go from Authorisation Type Page to Authorisation Reference Number Page" in {
          navigator
            .nextPage(AuthorisationTypePage(index), mode, emptyUserAnswers)
            .mustBe(idAuthRoutes.AuthorisationReferenceNumberController.onPageLoad(emptyUserAnswers.mrn, index, mode))
        }

        "must go from is Add Another Page" - {
          "when Yes selected" - {
            "to Authorisation Type Page" in {
              val userAnswers = emptyUserAnswers.setValue(AddAnotherAuthorisationPage, true)
              navigator
                .nextPage(AddAnotherAuthorisationPage, mode, userAnswers)
                .mustBe(idAuthRoutes.AuthorisationTypeController.onPageLoad(userAnswers.mrn, index, mode))
            }
          }

          "when No selected" - {
            "to Identification Number page" in {
              val userAnswers = emptyUserAnswers.setValue(AddAnotherAuthorisationPage, false)
              navigator
                .nextPage(AddAnotherAuthorisationPage, mode, userAnswers)
                .mustBe(idRoutes.IdentificationNumberController.onPageLoad(userAnswers.mrn, mode))
            }
          }

          "when nothing selected" - {
            "to session expired" in {
              navigator
                .nextPage(AddAnotherAuthorisationPage, mode, emptyUserAnswers)
                .mustBe(controllers.routes.SessionExpiredController.onPageLoad())
            }
          }
        }
      }
    }
  }
}
