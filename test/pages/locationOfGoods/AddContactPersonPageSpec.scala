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

package pages.locationOfGoods

import org.scalacheck.Gen
import pages.behaviours.PageBehaviours

class AddContactPersonPageSpec extends PageBehaviours {

  "AddContactPersonPage" - {

    beRetrievable[Boolean](AddContactPersonPage)

    beSettable[Boolean](AddContactPersonPage)

    beRemovable[Boolean](AddContactPersonPage)

    "cleanup" - {
      "when no selected" - {
        "must remove name and phone number" in {
          val userAnswers = emptyUserAnswers
            .setValue(AddContactPersonPage, true)
            .setValue(ContactPersonNamePage, Gen.alphaNumStr.sample.value)
            .setValue(ContactPersonTelephonePage, Gen.alphaNumStr.sample.value)

          val result = userAnswers.setValue(AddContactPersonPage, false)

          result.get(ContactPersonNamePage) must not be defined
          result.get(ContactPersonTelephonePage) must not be defined
        }
      }
    }
  }
}
