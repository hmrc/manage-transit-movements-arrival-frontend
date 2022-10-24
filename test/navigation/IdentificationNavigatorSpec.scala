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
import pages.incident.IncidentFlagPage

class IdentificationNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "Identification Navigator" - {

    "in NormalMode" - {

      val navigator = new IdentificationNavigator(NormalMode)

      "when answers complete" - {
        "must redirect to location of goods type page" in {
          forAll(arbitraryIdentificationAnswers(emptyUserAnswers)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.locationOfGoods.routes.TypeOfLocationController.onPageLoad(answers.mrn, NormalMode))
          }
        }
      }
    }
    // TODO: Add final check your answers page (CTCP-702)
    "in CheckMode" - {

      "when answers complete" - {
        "must redirect to check your answers" in {
          val ua = emptyUserAnswers.setValue(IncidentFlagPage, false)
          forAll(arbitraryArrivalAnswers(ua), CheckMode) {
            (answers, mode) =>
              val navigatorProvider = new IdentificationNavigatorProviderImpl()
              val navigator         = navigatorProvider.apply(mode)

              navigator
                .nextPage(answers)
                .mustBe(controllers.routes.CheckTransitionArrivalsAnswersController.onPageLoad(answers.mrn))
          }
        }
      }
    }
  }
}
