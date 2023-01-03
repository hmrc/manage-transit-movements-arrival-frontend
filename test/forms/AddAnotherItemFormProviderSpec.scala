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

package forms

import forms.behaviours.BooleanFieldBehaviours
import org.scalacheck.Gen

class AddAnotherItemFormProviderSpec extends BooleanFieldBehaviours {

  private val prefix = Gen.alphaNumStr.sample.value
  private val form   = new AddAnotherItemFormProvider()

  ".value" - {

    val fieldName = "value"

    "when max limit hit" - {
      "must bind true" in {
        val result = form(prefix, false).bind(Map(fieldName -> "true"))
        result.value.value mustBe false
      }

      "must bind false to true" in {
        val result = form(prefix, false).bind(Map(fieldName -> "false"))
        result.value.value mustBe false
      }

      "must bind blank to true" in {
        val result = form(prefix, false).bind(Map.empty[String, String])
        result.value.value mustBe false
      }
    }
  }
}
