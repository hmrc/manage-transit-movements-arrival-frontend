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

package navigation

import base.SpecBase
import generators.Generators
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class EquipmentNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "Equipment Navigator" - {

    "when in NormalMode" - {

      val mode              = NormalMode
      val navigatorProvider = new EquipmentNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, incidentIndex, equipmentIndex)

      "when answers complete" - {
        "must redirect to equipment check your answers page" in {
          forAll(arbitraryEquipmentAnswers(emptyUserAnswers, incidentIndex, equipmentIndex)) {
            answers =>
              navigator
                .nextPage(answers, None)
                .mustBe(controllers.incident.equipment.routes.CheckEquipmentAnswersController.onPageLoad(answers.mrn, mode, incidentIndex, equipmentIndex))
          }
        }
      }
    }

    "when in CheckMode" - {

      val mode              = CheckMode
      val navigatorProvider = new EquipmentNavigatorProviderImpl
      val navigator         = navigatorProvider.apply(mode, incidentIndex, equipmentIndex)

      "when answers complete" - {
        "must redirect to incident answers page" in {
          forAll(arbitraryEquipmentsAnswers(emptyUserAnswers, incidentIndex)) {
            answers =>
              navigator
                .nextPage(answers, None)
                .mustBe(controllers.incident.equipment.routes.AddAnotherEquipmentController.onPageLoad(answers.mrn, mode, incidentIndex))
          }
        }
      }
    }
  }
}
