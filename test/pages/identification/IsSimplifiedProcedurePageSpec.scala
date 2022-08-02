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

package pages.identification

import models.Index
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.identification.authorisation.AuthorisationReferenceNumberPage

class IsSimplifiedProcedurePageSpec extends PageBehaviours {

  "IsSimplifiedProcedurePage" - {

    beRetrievable[Boolean](IsSimplifiedProcedurePage)

    beSettable[Boolean](IsSimplifiedProcedurePage)

    beRemovable[Boolean](IsSimplifiedProcedurePage)

    "cleanup" - {
      "when NO selected" - {
        "must clean up IdentificationAuthorisationSection" in {
          forAll(arbitrary[String]) {
            refNo =>
              val preChange  = emptyUserAnswers.setValue(AuthorisationReferenceNumberPage(Index(0)), refNo)
              val postChange = preChange.setValue(IsSimplifiedProcedurePage, false)

              postChange.get(AuthorisationReferenceNumberPage(Index(0))) mustNot be(defined)
          }
        }
      }

      "when YES selected" - {
        "must do nothing" in {
          forAll(arbitrary[String]) {
            refNo =>
              val preChange  = emptyUserAnswers.setValue(AuthorisationReferenceNumberPage(Index(0)), refNo)
              val postChange = preChange.setValue(IsSimplifiedProcedurePage, true)

              postChange.get(AuthorisationReferenceNumberPage(Index(0))) must be(defined)
          }
        }
      }
    }
  }
}
