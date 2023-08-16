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

import forms.Constants.maxIncidentTextLength
import forms.behaviours.StringFieldBehaviours
import forms.incident.IncidentTextFormProvider
import models.domain.StringFieldRegex.stringFieldRegexComma
import org.scalacheck.Gen
import play.api.data.{Field, FormError}

class IncidentTextFormProviderSpec extends StringFieldBehaviours {

  private val prefix       = Gen.alphaNumStr.sample.value
  private val requiredKey  = s"$prefix.error.required"
  private val invalidKey   = s"$prefix.error.invalidCharacters"
  private val maxLengthKey = s"$prefix.error.maxLength"
  private val maxLength    = maxIncidentTextLength

  val form = new IncidentTextFormProvider()(prefix)

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(stringFieldRegexComma.regex)),
      length = maxLength
    )

    "must not bind valid strings over max length" in {
      val expectedError = FormError(fieldName, maxLengthKey, Seq(maxLength))

      val gen = for {
        str <- stringsLongerThan(maxLength, Gen.alphaNumChar)
      } yield str

      forAll(gen) {
        invalidString =>
          val result: Field = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
          result.errors must contain(expectedError)
      }
    }
  }
}
