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
import generators.{ArrivalUserAnswersGenerator, Generators}
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class LocationOfGoodsNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "Location Of Goods Navigator" - {

    "in NormalMode" - {

      val mode = NormalMode

      "when answers complete" - {
        "when pre-transition" ignore {

          val navigatorProvider = new LocationOfGoodsNavigatorProviderImpl()
          val navigator         = navigatorProvider.apply(mode)

          "must redirect to arrival answers" in {
            forAll(arbitraryIdentificationAnswers(emptyUserAnswers)) {
              initialAnswers =>
                forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
                  answers =>
                    navigator
                      .nextPage(answers)
                      .mustBe(controllers.routes.CheckArrivalsAnswersController.onPageLoad(answers.mrn))
                }
            }
          }
        }

        "when post-transition" - {

          val navigatorProvider = new LocationOfGoodsNavigatorProviderImpl()
          val navigator         = navigatorProvider.apply(mode)

          "must redirect to incident flag page" in {
            forAll(arbitraryIdentificationAnswers(emptyUserAnswers)) {
              initialAnswers =>
                forAll(arbitraryLocationOfGoodsAnswers(initialAnswers)) {
                  answers =>
                    navigator
                      .nextPage(answers)
                      .mustBe(controllers.incident.routes.IncidentFlagController.onPageLoad(answers.mrn, mode))
                }
            }
          }
        }
      }
    }

    "in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new IdentificationNavigatorProviderImpl()
      val navigator         = navigatorProvider.apply(mode)

      "when answers complete" - {
        "must redirect to check your answers" in {
          forAll(arbitraryArrivalAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.routes.CheckArrivalsAnswersController.onPageLoad(answers.mrn))
          }
        }
      }
    }
  }
}
