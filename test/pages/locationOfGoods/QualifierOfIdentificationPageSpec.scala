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

import forms.Constants.CustomsOfficeCode
import models.UserAnswers
import models.reference.QualifierOfIdentification
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.sections.locationOfGoods.{ContactPersonSection, QualifierOfIdentificationDetailsSection}
import play.api.libs.json.Json

class QualifierOfIdentificationPageSpec extends PageBehaviours {

  "QualifierOfIdentificationPage" - {

    beRetrievable[QualifierOfIdentification](QualifierOfIdentificationPage)

    beSettable[QualifierOfIdentification](QualifierOfIdentificationPage)

    beRemovable[QualifierOfIdentification](QualifierOfIdentificationPage)

    "cleanup" - {

      "must remove previous answers when given a new answer" in {

        val sampleUa = arbitrary[UserAnswers].sample.value

        forAll(arbitrary[QualifierOfIdentification]) {
          qualifierOfIdentification =>
            forAll(arbitrary[QualifierOfIdentification].retryUntil(_.qualifier != qualifierOfIdentification.qualifier)) {
              differentQualifierOfIdentification =>
                val result = sampleUa
                  .setValue(QualifierOfIdentificationPage, qualifierOfIdentification)
                  .setValue(QualifierOfIdentificationDetailsSection, Json.obj("foo" -> "bar"))
                  .setValue(QualifierOfIdentificationPage, differentQualifierOfIdentification)

                result.get(QualifierOfIdentificationDetailsSection) must not be defined
            }
        }
      }

      "must remove contact person when answer is customs office" in {
        forAll(arbitrary[String], arbitrary[String]) {
          (name, telephoneNumber) =>
            val userAnswers = emptyUserAnswers
              .setValue(AddContactPersonPage, true)
              .setValue(ContactPersonNamePage, name)
              .setValue(ContactPersonTelephonePage, telephoneNumber)

            val result = userAnswers.setValue(QualifierOfIdentificationPage, qualifierOfIdentificationGen(CustomsOfficeCode).sample.value)

            result.get(AddContactPersonPage) must not be defined
            result.get(ContactPersonSection) must not be defined
        }
      }

      "must not remove previous answers when given the same answer" in {

        val sampleUa = arbitrary[UserAnswers].sample.value

        forAll(arbitrary[QualifierOfIdentification]) {
          qualifierOfIdentification =>
            val result = sampleUa
              .setValue(QualifierOfIdentificationPage, qualifierOfIdentification)
              .setValue(QualifierOfIdentificationDetailsSection, Json.obj("foo" -> "bar"))
              .setValue(QualifierOfIdentificationPage, qualifierOfIdentification)

            result.get(QualifierOfIdentificationDetailsSection) mustBe defined
        }
      }

    }

  }
}
