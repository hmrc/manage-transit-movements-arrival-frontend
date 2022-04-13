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

package pages.events

import models.{TranshipmentType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.events.transhipments.TranshipmentTypePage

class IsTranshipmentPageSpec extends PageBehaviours {

  "IsTranshipmentPage" - {

    beRetrievable[Boolean](IsTranshipmentPage(eventIndex))

    beSettable[Boolean](IsTranshipmentPage(eventIndex))

    beRemovable[Boolean](IsTranshipmentPage(eventIndex))

    "cleanup" - {
      "must remove incident information data when there is a change of the answer to 'Yes'" in {

        forAll(arbitrary[UserAnswers], arbitrary[String]) {
          (userAnswers, incidentInfo) =>
            val result = userAnswers
              .setValue(IsTranshipmentPage(eventIndex), false)
              .setValue(IncidentInformationPage(eventIndex), incidentInfo)
              .setValue(IsTranshipmentPage(eventIndex), true)

            result.get(IncidentInformationPage(eventIndex)) must not be defined
        }
      }

      "must remove transhipment type data when is a change of the answer to 'No'" in {

        forAll(arbitrary[UserAnswers], arbitrary[TranshipmentType]) {
          (userAnswers, transhipmentType) =>
            val result = userAnswers
              .setValue(IsTranshipmentPage(eventIndex), true)
              .setValue(TranshipmentTypePage(eventIndex), transhipmentType)
              .setValue(IsTranshipmentPage(eventIndex), false)

            result.get(TranshipmentTypePage(eventIndex)) must not be defined
        }
      }

      "must remove incident information data when there is no answer" in {

        forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[TranshipmentType]) {
          (userAnswers, incidentInfo, transhipmentType) =>
            val result = userAnswers
              .setValue(IncidentInformationPage(eventIndex), incidentInfo)
              .setValue(TranshipmentTypePage(eventIndex), transhipmentType)
              .removeValue(IsTranshipmentPage(eventIndex))

            result.get(IncidentInformationPage(eventIndex)) must not be defined
            result.get(TranshipmentTypePage(eventIndex)) must not be defined
        }
      }
    }

  }
}
