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
import models.UkAddressLine._
import org.scalacheck.Gen
import play.api.data.FormError

class UkAddressFormProviderSpec extends StringFieldBehaviours with SpecBase {

  private val prefix = Gen.alphaNumStr.sample.value
  private val name   = Gen.alphaNumStr.sample.value

  private val requiredKey = s"$prefix.error.required"
  private val lengthKey   = s"$prefix.error.length"
  private val invalidKey  = s"$prefix.error.invalid"

  private val form = new UkAddressFormProvider()(prefix, name)

  ".buildingAndStreet" - {

    val fieldName = BuildingAndStreet.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(BuildingAndStreet.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = BuildingAndStreet.length,
      lengthError = FormError(fieldName, lengthKey, Seq(BuildingAndStreet.arg.capitalize, name, BuildingAndStreet.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(BuildingAndStreet.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(BuildingAndStreet.arg.capitalize, name)),
      length = BuildingAndStreet.length
    )
  }

  ".city" - {

    val fieldName = City.field

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(City.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = City.length,
      lengthError = FormError(fieldName, lengthKey, Seq(City.arg.capitalize, name, City.length))
    )

    behave like mandatoryTrimmedField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(City.arg, name))
    )

    behave like fieldWithInvalidCharacters(
      form = form,
      fieldName = fieldName,
      error = FormError(fieldName, invalidKey, Seq(City.arg.capitalize, name)),
      length = City.length
    )
  }

  ".postCode" - {

    val postcodeInvalidKey    = s"$prefix.error.postalCode.invalid"
    val postalCodeRequiredKey = s"$prefix.error.postalCode.required"
    val lengthKey             = s"$prefix.error.postalCode.length"

    val fieldName = PostCode.field

    val validPostalOverLength: Gen[String] = for {
      num  <- Gen.chooseNum[Int](PostCode.length + 1, PostCode.length + 5)
      list <- Gen.listOfN(num, Gen.alphaNumChar)
    } yield list.mkString("")

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(PostCode.length)
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = PostCode.length,
      lengthError = FormError(fieldName, lengthKey, Seq(name, PostCode.length)),
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
      length = PostCode.length
    )
  }
}
