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

import pages.behaviours.PageBehaviours
import pages.incident
import pages.sections.incident.EquipmentsSection
import play.api.libs.json.{JsArray, Json}

class AddTransportEquipmentPageSpec extends PageBehaviours {

  "AddTransportEquipmentPage" - {

    beRetrievable[Boolean](AddTransportEquipmentPage(index))

    beSettable[Boolean](incident.AddTransportEquipmentPage(index))

    beRemovable[Boolean](incident.AddTransportEquipmentPage(index))
  }

  "cleanup" - {
    "when no is selected" - {
      "must remove equipments section" in {
        val userAnswers = emptyUserAnswers
          .setValue(EquipmentsSection(index), JsArray(Seq(Json.obj("foo" -> "bar"))))

        val result = userAnswers.setValue(incident.AddTransportEquipmentPage(index), false)

        result.get(EquipmentsSection(index)) mustNot be(defined)
      }
    }
  }
}
