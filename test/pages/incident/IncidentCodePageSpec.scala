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
import pages.behaviours.PageBehaviours
import pages.sections.incident.EquipmentsSection
import play.api.libs.json.{JsArray, Json}
import org.scalacheck.Arbitrary.arbitrary

class IncidentCodePageSpec extends PageBehaviours {

  "IncidentCodePage" - {

    beRetrievable[IncidentCode](IncidentCodePage(index))

    beSettable[IncidentCode](IncidentCodePage(index))

    beRemovable[IncidentCode](IncidentCodePage(index))
  }

  "cleanup" - {
    "when code is changed" in {
      forAll(arbitrary[IncidentCode]) {
        incidentCode =>
          val userAnswers = emptyUserAnswers
            .setValue(EquipmentsSection(index), JsArray(Seq(Json.obj("foo" -> "bar"))))

          val result = userAnswers.setValue(IncidentCodePage(index), incidentCode)

          result.get(EquipmentsSection(index)) mustNot be(defined)
      }

    }
  }
}
