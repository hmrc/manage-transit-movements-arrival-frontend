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

import forms.mappings.Mappings
import models.MovementReferenceNumber
import play.api.data.Form

import javax.inject.Inject

class MovementReferenceNumberFormProvider @Inject() extends Mappings {

  private val requiredKey: String         = "movementReferenceNumber.error.required"
  private val lengthKey: String           = "movementReferenceNumber.error.length"
  private val invalidCharacterKey: String = "movementReferenceNumber.error.invalidCharacter"
  private val invalidMRNKey: String       = "movementReferenceNumber.error.invalidMRN"

  def apply(): Form[MovementReferenceNumber] =
    Form(
      "value" -> mrn(
        requiredKey,
        lengthKey,
        invalidCharacterKey,
        invalidMRNKey
      )
    )

  def applyUnsafe(): Form[MovementReferenceNumber] =
    Form(
      "value" -> mrnUnsafe(
        requiredKey,
        lengthKey,
        invalidCharacterKey,
        invalidMRNKey
      )
    )
}
