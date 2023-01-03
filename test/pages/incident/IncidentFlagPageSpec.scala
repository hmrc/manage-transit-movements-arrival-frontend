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

package pages.incident

import pages.behaviours.PageBehaviours
import pages.sections.incident.IncidentsSection
import play.api.libs.json.{JsArray, JsString}

class IncidentFlagPageSpec extends PageBehaviours {

  "IncidentFlagPage" - {

    beRetrievable[Boolean](IncidentFlagPage)

    beSettable[Boolean](IncidentFlagPage)

    beRemovable[Boolean](IncidentFlagPage)

    "cleanup" - {
      "when no selected" - {
        "must remove incidents and endorsements" in {
          val userAnswers = emptyUserAnswers
            .setValue(IncidentFlagPage, true)
            .setValue(IncidentsSection, JsArray(Seq(JsString("foo"), JsString("bar"))))

          val result = userAnswers.setValue(IncidentFlagPage, false)

          result.get(IncidentsSection) must not be defined
        }
      }
    }
  }
}
