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

import models.DynamicAddress
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class CountryPageSpec extends PageBehaviours {

  "CountryPage" - {

    beRetrievable[Country](CountryPage)

    beSettable[Country](CountryPage)

    beRemovable[Country](CountryPage)

    "cleanup" - {
      "must remove address page" - {
        "when answer changes" in {
          def country = arbitrary[Country].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(CountryPage, country)
            .setValue(AddressPage, arbitrary[DynamicAddress].sample.value)

          val result = userAnswers.setValue(CountryPage, country)

          result.get(AddressPage) must not be defined
        }
      }

      "must not remove address page" - {
        "when answer does not change" in {
          val country = arbitrary[Country].sample.value

          val userAnswers = emptyUserAnswers
            .setValue(CountryPage, country)
            .setValue(AddressPage, arbitrary[DynamicAddress].sample.value)

          val result = userAnswers.setValue(CountryPage, country)

          result.get(AddressPage) must be(defined)
        }
      }
    }
  }
}
