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

package pages.events.seals

import base.SpecBase
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import queries.SealsQuery

class HaveSealsChangedPageSpec extends PageBehaviours with SpecBase {

  "HaveSealsChangedPage" - {

    beRetrievable[Boolean](HaveSealsChangedPage(eventIndex))

    beSettable[Boolean](HaveSealsChangedPage(eventIndex))

    beRemovable[Boolean](HaveSealsChangedPage(eventIndex))

    "clean up" - {
      "must remove seals when user answer changes from 'Yes' to 'No' " in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .setValue(HaveSealsChangedPage(eventIndex), true)
              .setValue(SealIdentityPage(eventIndex, sealIndex), sealDomain)
              .setValue(HaveSealsChangedPage(eventIndex), false)

            result.get(SealIdentityPage(eventIndex, sealIndex)) must not be defined
            result.get(SealsQuery(eventIndex)) must not be defined
        }
      }

      "must not remove seals when user answer changes from 'No' to 'Yes' " in {

        forAll(arbitrary[UserAnswers]) {
          userAnswers =>
            val result = userAnswers
              .setValue(HaveSealsChangedPage(eventIndex), false)
              .setValue(SealIdentityPage(eventIndex, sealIndex), sealDomain)
              .setValue(HaveSealsChangedPage(eventIndex), true)

            result.get(SealIdentityPage(eventIndex, sealIndex)) mustBe defined
            result.get(SealsQuery(eventIndex)) mustBe defined
        }
      }
    }
  }
}
