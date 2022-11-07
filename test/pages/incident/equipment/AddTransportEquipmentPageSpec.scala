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

package pages.incident.equipment

import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary

class AddTransportEquipmentPageSpec extends PageBehaviours {

  "AddTransportEquipmentPage" - {

    beRetrievable[Boolean](AddTransportEquipmentPage(index))

    beSettable[Boolean](AddTransportEquipmentPage(index))

    beRemovable[Boolean](AddTransportEquipmentPage(index))
  }

  "cleanup" - {
    "when no is selected" - {
      "must remove ContainerIdentificationYesNoPage and ContainerIdentificationNumberPage" in {
        forAll(arbitrary[String]) {
          str =>
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIdentificationNumberYesNoPage(index), true)
              .setValue(ContainerIdentificationNumberPage(index), str)

            val result = userAnswers.setValue(AddTransportEquipmentPage(index), false)

            result.get(ContainerIdentificationNumberYesNoPage(index)) mustNot be(defined)
            result.get(ContainerIdentificationNumberPage(index)) mustNot be(defined)
        }
      }
    }
  }
}
