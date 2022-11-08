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

package pages.incident

import models.Index
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.incident
import pages.incident.equipment._

class ContainerIndicatorYesNoPageSpec extends PageBehaviours {

  "ContainerIndicatorYesNoPage" - {

    beRetrievable[Boolean](ContainerIndicatorYesNoPage(index))

    beSettable[Boolean](ContainerIndicatorYesNoPage(index))

    beRemovable[Boolean](ContainerIndicatorYesNoPage(index))
  }

  "cleanup" - {
    "when no is selected" - {
      "must clean up ContainerIdentificationNumberPage at every index" in {
        forAll(arbitrary[String]) {
          str =>
            val preChange = emptyUserAnswers
              .setValue(ContainerIdentificationNumberPage(incidentIndex, Index(0)), str)
              .setValue(ContainerIdentificationNumberPage(incidentIndex, Index(1)), str)

            val postChange = preChange.setValue(incident.ContainerIndicatorYesNoPage(incidentIndex), false)

            postChange.get(ContainerIdentificationNumberPage(incidentIndex, Index(0))) mustNot be(defined)
            postChange.get(ContainerIdentificationNumberPage(incidentIndex, Index(1))) mustNot be(defined)
        }
      }
    }

    "when yes is selected" - {
      "must remove Add transport equipment page and add container identification number page at index 0" in {
        forAll(arbitrary[String]) {
          str =>
            val preChange = emptyUserAnswers
              .setValue(AddTransportEquipmentPage(incidentIndex), true)
              .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, Index(0)), true)
              .setValue(ContainerIdentificationNumberPage(incidentIndex, Index(0)), str)

            val postChange = preChange.setValue(incident.ContainerIndicatorYesNoPage(index), true)

            postChange.get(incident.AddTransportEquipmentPage(incidentIndex)) mustNot be(defined)
            postChange.get(ContainerIdentificationNumberYesNoPage(incidentIndex, Index(0))) mustNot be(defined)
            postChange.get(ContainerIdentificationNumberPage(incidentIndex, Index(0))) must be(defined)
        }
      }
    }
  }
}
