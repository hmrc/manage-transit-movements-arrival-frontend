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

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class EventReportedPageSpec extends PageBehaviours {

  "EventReportedPage" - {

    beRetrievable[Boolean](EventReportedPage(eventIndex))

    beSettable[Boolean](EventReportedPage(eventIndex))

    beRemovable[Boolean](EventReportedPage(eventIndex))

    "cleanup" - {
      "must remove incident information when IsTranshipmentPage is change to true" in {
        forAll(arbitrary[UserAnswers], arbitrary[Boolean], arbitrary[String]) {
          (userAnswers, eventReportedAnswer, incidentInformationAnswer) =>
            val ua = userAnswers
              .setValue(IsTranshipmentPage(eventIndex), false)
              .setValue(IncidentInformationPage(eventIndex), incidentInformationAnswer)
              .setValue(IsTranshipmentPage(eventIndex), true)

            val result = ua.setValue(EventReportedPage(eventIndex), eventReportedAnswer)

            result.getValue(IsTranshipmentPage(eventIndex)) mustEqual true
            result.get(IncidentInformationPage(eventIndex)) must not be defined
        }
      }

      "must remove incident information data when EventReportedPage changes to true, IsTranshipmentPage is false, and the user has answered information" in {
        forAll(arbitrary[UserAnswers], arbitrary[String]) {
          (userAnswers, incidentInformation) =>
            val result = userAnswers
              .setValue(EventReportedPage(eventIndex), false)
              .setValue(IsTranshipmentPage(eventIndex), false)
              .setValue(IncidentInformationPage(eventIndex), incidentInformation)
              .setValue(EventReportedPage(eventIndex), true)

            result.getValue(IsTranshipmentPage(eventIndex)) mustEqual false
            result.get(IncidentInformationPage(eventIndex)) must not be defined
        }
      }

      "must not remove incident information data when EventReportedPage is false, IsTranshipmentPage is false, and the user has answered information" in {
        forAll(arbitrary[UserAnswers], arbitrary[String]) {
          (userAnswers, incidentInformation) =>
            val ua = userAnswers
              .setValue(IsTranshipmentPage(eventIndex), false)
              .setValue(IncidentInformationPage(eventIndex), incidentInformation)

            val result = ua.setValue(EventReportedPage(eventIndex), false)

            result.getValue(IsTranshipmentPage(eventIndex)) mustEqual false
            result.get(IncidentInformationPage(eventIndex)) must be(defined)
        }
      }

    }

  }
}
