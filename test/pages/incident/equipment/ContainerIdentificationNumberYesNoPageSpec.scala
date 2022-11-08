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

import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class ContainerIdentificationNumberYesNoPageSpec extends PageBehaviours {

  "ContainerIdentificationNumberYesNoPage" - {

    beRetrievable[Boolean](ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex))

    beSettable[Boolean](ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex))

    beRemovable[Boolean](ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex))
  }

  "cleanup" - {
    "when no is selected" - {
      "must clean up ContainerIdentificationNumberPage" in {
        forAll(arbitrary[String]) {
          str =>
            val preChange = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), str)

            val postChange = preChange.setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), false)

            postChange.get(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)) mustNot be(defined)
        }
      }
    }

    "when yes is selected" - {
      "must do nothing" in {
        forAll(arbitrary[String]) {
          str =>
            val preChange = emptyUserAnswers.setValue(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex), str)

            val postChange = preChange.setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

            postChange.get(ContainerIdentificationNumberPage(incidentIndex, equipmentIndex)) must be(defined)
        }
      }
    }
  }
}
