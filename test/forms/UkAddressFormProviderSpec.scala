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

package forms

import base.SpecBase
import forms.behaviours.StringFieldBehaviours
import models.AddressLine._
import org.scalacheck.Gen
import play.api.data.FormError

class UkAddressFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value
  private val name   = Gen.alphaNumStr.sample.value

  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"
  private val invalidKey  = s"$prefix.error.invalid"

  private val form = new UkAddressFormProvider()(prefix, name)

  ".addressLine1" - {

    val fieldName = AddressLine1.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine1.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine1.length,
      lengthError = FormError(fieldName, lengthKey, Seq(AddressLine1.arg.capitalize, name, AddressLine1.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(AddressLine1.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(AddressLine1.arg.capitalize, name)),
      length = AddressLine1.length
    )
  }

  ".addressLine2" - {

    val fieldName = AddressLine2.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(AddressLine2.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = AddressLine2.length,
      lengthError = FormError(fieldName, lengthKey, Seq(AddressLine2.arg.capitalize, name, AddressLine2.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(AddressLine2.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(AddressLine2.arg.capitalize, name)),
      length = AddressLine2.length
    )
  }

  ".postalCode" - {

    val postcodeInvalidKey    = s"$prefix.error.postalCode.invalid"
    val postalCodeRequiredKey = s"$prefix.error.postalCode.required"
    val lengthKey             = s"$prefix.error.postalCode.length"

    val fieldName = UkPostCode.field

    val validPostalOverLength: Gen[String] = for {
      num  <- Gen.chooseNum[Int](UkPostCode.length + 1, UkPostCode.length + 5)
      list <- Gen.listOfN(num, Gen.alphaNumChar)
    } yield list.mkString("")

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(UkPostCode.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = UkPostCode.length,
      lengthError = FormError(fieldName, lengthKey, Seq(name, UkPostCode.length)),
      gen = validPostalOverLength
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, postalCodeRequiredKey, Seq(name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, postcodeInvalidKey, Seq(name)),
      length = UkPostCode.length
    )
  }
}
