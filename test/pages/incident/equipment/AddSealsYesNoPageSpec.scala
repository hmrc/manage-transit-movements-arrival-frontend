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

package pages.incident.equipment

import pages.behaviours.PageBehaviours
import pages.sections.incident.SealsSection
import play.api.libs.json.{JsArray, Json}

class AddSealsYesNoPageSpec extends PageBehaviours {

  "AddSealsYesNoPage" - {

    beRetrievable[Boolean](AddSealsYesNoPage(incidentIndex, equipmentIndex))

    beSettable[Boolean](AddSealsYesNoPage(incidentIndex, equipmentIndex))

    beRemovable[Boolean](AddSealsYesNoPage(incidentIndex, equipmentIndex))
  }

  "cleanup" - {
    "when changed to false" - {
      "Must remove the seals section" in {
        val userAnswers = emptyUserAnswers
          .setValue(SealsSection(incidentIndex, equipmentIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

        val result = userAnswers.setValue(AddSealsYesNoPage(incidentIndex, equipmentIndex), false)

        result.get(SealsSection(incidentIndex, equipmentIndex)) must not be defined
      }
    }
  }
}
