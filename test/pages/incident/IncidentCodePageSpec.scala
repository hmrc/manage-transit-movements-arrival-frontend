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

import models.incident.IncidentCode
import models.incident.IncidentCode._
import org.scalacheck.Gen
import pages.behaviours.PageBehaviours
import pages.incident.equipment.{AddTransportEquipmentPage, ContainerIndicatorYesNoPage}
import pages.sections.incident.EquipmentSection
import play.api.libs.json.Json

class IncidentCodePageSpec extends PageBehaviours {

  "IncidentCodePage" - {

    beRetrievable[IncidentCode](IncidentCodePage(index))

    beSettable[IncidentCode](IncidentCodePage(index))

    beRemovable[IncidentCode](IncidentCodePage(index))
  }

  "cleanup" - {
    "when code is changed" - {
      "to option 2 or 4 " in {
        forAll(Gen.oneOf(SealsBrokenOrTampered, PartiallyOrFullyUnloaded)) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(ContainerIndicatorYesNoPage(index), false)
              .setValue(AddTransportEquipmentPage(index), false)

            val result = userAnswers.setValue(IncidentCodePage(index), incidentCode)

            result.get(ContainerIndicatorYesNoPage(index)) mustNot be(defined)
            result.get(AddTransportEquipmentPage(index)) mustNot be(defined)
        }
      }

      "to option 1 or 5 " in {
        forAll(Gen.oneOf(DeviatedFromItinerary, CarrierUnableToComply)) {
          incidentCode =>
            val userAnswers = emptyUserAnswers
              .setValue(EquipmentSection(index), Json.obj("foo" -> "bar"))

            val result = userAnswers.setValue(IncidentCodePage(index), incidentCode)

            result.get(EquipmentSection(index)) mustNot be(defined)
        }
      }

    }
  }
}
