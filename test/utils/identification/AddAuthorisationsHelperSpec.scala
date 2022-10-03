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

package utils.identification

import base.SpecBase
import controllers.identification.authorisation.{routes => authRoutes}
import generators.Generators
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.identification.authorisation.AuthorisationType._
import models.{Index, NormalMode}
import org.scalacheck.Gen
import pages.identification.IsSimplifiedProcedurePage
import pages.identification.authorisation._
import viewModels.ListItem

class AddAuthorisationsHelperSpec extends SpecBase with Generators {

  "listItems" - {

    "when empty user answers" - {
      "must return empty list of list items" in {
        val userAnswers = emptyUserAnswers

        val helper = new AddAuthorisationHelper(userAnswers, NormalMode)
        helper.listItems mustBe Nil
      }
    }

    "when user answers populated with a complete authorisation" - {
      "must return one list item" in {
        val ref = Gen.alphaNumStr.sample.value
        val userAnswers = emptyUserAnswers
          .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
          .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.ACE)
          .setValue(AuthorisationReferenceNumberPage(Index(0)), ref)

        val helper = new AddAuthorisationHelper(userAnswers, NormalMode)
        helper.listItems mustBe Seq(
          Right(
            ListItem(
              name = s"ACE - $ref",
              changeUrl = authRoutes.CheckAuthorisationAnswersController.onPageLoad(userAnswers.mrn, Index(0)).url,
              removeUrl = Some(authRoutes.ConfirmRemoveAuthorisationController.onPageLoad(userAnswers.mrn, Index(0)).url)
            )
          )
        )
      }
    }
  }

}
