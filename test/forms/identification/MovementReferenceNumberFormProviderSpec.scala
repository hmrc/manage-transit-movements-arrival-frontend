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

package forms.identification

import base.AppWithDefaultMockFixtures
import forms.behaviours.StringFieldBehaviours
import models.MovementReferenceNumber
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.FormError

class MovementReferenceNumberFormProviderSpec extends StringFieldBehaviours with AppWithDefaultMockFixtures {

  private val requiredKey         = "movementReferenceNumber.error.required"
  private val lengthKey           = "movementReferenceNumber.error.length"
  private val invalidCharacterKey = "movementReferenceNumber.error.invalidCharacter"
  private val invalidMRNKey       = "movementReferenceNumber.error.invalidMRN"

  private val mrnLength = MovementReferenceNumber.Constants.length
  val form              = new MovementReferenceNumberFormProvider()()

  ".value" - {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      arbitrary[MovementReferenceNumber].map(_.toString)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithExactLength(
      form,
      fieldName,
      exactLength = mrnLength,
      lengthError = FormError(fieldName, lengthKey)
    )

    "must not bind MRN with invalid characters" in {
      val str    = List.fill(mrnLength)("§").mkString
      val result = form.bind(Map(fieldName -> str)).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, invalidCharacterKey))
    }

    "must not bind MRN with invalid format" in {
      val str    = "51GBLFUWH7WOI085M6"
      val result = form.bind(Map(fieldName -> str)).apply(fieldName)
      result.errors mustEqual Seq(FormError(fieldName, invalidMRNKey))
    }

    "must bind valid MRN with spaces" in {
      val str    = "51 GB LFUWH7WOI085M 4"
      val result = form.bind(Map(fieldName -> str)).apply(fieldName)
      result.value.value mustEqual str
    }
  }
}
