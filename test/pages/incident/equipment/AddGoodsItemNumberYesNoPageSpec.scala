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
import pages.sections.incident.ItemsSection
import play.api.libs.json.{JsArray, Json}

class AddGoodsItemNumberYesNoPageSpec extends PageBehaviours {

  "AddGoodsItemNumberYesNoPage" - {

    beRetrievable[Boolean](AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex))

    beSettable[Boolean](AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex))

    beRemovable[Boolean](AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex))
  }

  "cleanup" - {
    "when changed to false" - {
      "Must remove the seals section" in {
        val userAnswers = emptyUserAnswers
          .setValue(ItemsSection(incidentIndex, equipmentIndex), JsArray(Seq(Json.obj("foo" -> "bar"))))

        val result = userAnswers.setValue(AddGoodsItemNumberYesNoPage(incidentIndex, equipmentIndex), false)

        result.get(ItemsSection(incidentIndex, equipmentIndex)) must not be defined
      }
    }
  }
}
