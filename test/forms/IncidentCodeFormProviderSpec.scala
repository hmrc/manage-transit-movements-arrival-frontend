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

import forms.behaviours.StringFieldBehaviours
import forms.incident.IncidentCodeFormProvider
import models.IncidentCodeList
import play.api.data.FormError
import generators.Generators
import org.scalacheck.Gen

class IncidentCodeFormProviderSpec extends StringFieldBehaviours with Generators {

  private val prefix      = Gen.alphaNumStr.sample.value
  private val requiredKey = s"$prefix.error.required"
  private val maxLength   = 8

  private val incidentCode1    = arbitraryIncidentCode.arbitrary.sample.get
  private val incidentCode2    = arbitraryIncidentCode.arbitrary.sample.get
  private val incidentCodeList = IncidentCodeList(Seq(incidentCode1, incidentCode2))

  private val form = new IncidentCodeFormProvider()(prefix, incidentCodeList)

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

    "not bind if customs office id does not exist in the incidentCodeList" in {
      val boundForm = form.bind(Map("value" -> "foobar"))
      val field     = boundForm("value")
      field.errors mustNot be(empty)
    }

    "bind a incidentCode id which is in the list" in {
      val boundForm = form.bind(Map("value" -> incidentCode1.code))
      val field     = boundForm("value")
      field.errors must be(empty)
    }
  }
}
