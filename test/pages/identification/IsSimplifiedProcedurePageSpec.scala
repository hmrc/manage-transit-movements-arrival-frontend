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

package pages.identification

import models.reference.QualifierOfIdentification
import models.identification.ProcedureType
import models.locationOfGoods.TypeOfLocation
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.locationOfGoods.{AddContactPersonPage, AuthorisationNumberPage, QualifierOfIdentificationPage, TypeOfLocationPage}

class IsSimplifiedProcedurePageSpec extends PageBehaviours {

  "IsSimplifiedProcedurePage" - {

    beRetrievable[ProcedureType]

    beSettable[ProcedureType]

    beRemovable[ProcedureType]

    "cleanup" - {
      "when normal procedure type selected" - {
        "must clean up IdentificationAuthorisationSection and QualifierOfIdentification section" in {
          forAll(arbitrary[String]) {
            refNo =>
              val preChange = emptyUserAnswers
                .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
                .setValue(AuthorisationReferenceNumberPage, refNo)
                .setValue(AuthorisationNumberPage, refNo)
                .setValue(AddContactPersonPage, true)

              val postChange = preChange.setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)

              postChange.get(AuthorisationReferenceNumberPage) mustNot be(defined)
              postChange.get(AuthorisationNumberPage) mustNot be(defined)
              postChange.get(AddContactPersonPage) mustBe defined

          }
        }
      }

      "when simplified procedure type selected" - {
        "must remove QualifierOfIdentification section" in {
          forAll(arbitrary[String]) {
            refNo =>
              val preChange = emptyUserAnswers
                .setValue(IsSimplifiedProcedurePage, ProcedureType.Normal)
                .setValue(TypeOfLocationPage, TypeOfLocation.DesignatedLocation)
                .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.AuthorisationNumber)
                .setValue(AuthorisationNumberPage, refNo)
                .setValue(AddContactPersonPage, true)

              val postChange = preChange.setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)

              postChange.get(AuthorisationReferenceNumberPage) mustNot be(defined)
              postChange.get(QualifierOfIdentificationPage) mustNot be(defined)
              postChange.get(AuthorisationNumberPage) mustNot be(defined)
              postChange.get(TypeOfLocationPage) mustNot be(defined)

              postChange.get(AddContactPersonPage) mustBe defined
          }
        }
      }
    }
  }
}
