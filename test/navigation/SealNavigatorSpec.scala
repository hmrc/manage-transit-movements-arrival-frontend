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

class SealNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  "Seal Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new SealNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, incidentIndex, equipmentIndex, sealIndex)

      "when answers complete" - {
        "must redirect to add another seal page" ignore {
          forAll(arbitrarySealAnswers(emptyUserAnswers, incidentIndex, equipmentIndex, sealIndex)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(controllers.incident.equipment.seal.routes.AddAnotherSealController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new SealNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, incidentIndex, equipmentIndex, sealIndex)

      "when answers complete" - {
        "must redirect to check your answers" ignore {
          forAll(arbitraryEquipmentAnswers(emptyUserAnswers, incidentIndex, equipmentIndex)) {
            answers =>
              navigator
                .nextPage(answers)
                .mustBe(???)
          }
        }
      }
    }
  }
}