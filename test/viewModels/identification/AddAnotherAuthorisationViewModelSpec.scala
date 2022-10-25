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

package viewModels.identification

import base.SpecBase
import generators.Generators
import models.Index
import models.identification.authorisation.AuthorisationType
import models.identification.authorisation.AuthorisationType._
import pages.identification.authorisation._
import viewModels.identification.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider

class AddAnotherAuthorisationViewModelSpec extends SpecBase with Generators {

  "must get list items" in {

    val userAnswers = emptyUserAnswers
      .setValue(AuthorisationTypePage(Index(0)), AuthorisationType.ACT)
      .setValue(AuthorisationReferenceNumberPage(Index(0)), "List item 1")
      .setValue(AuthorisationTypePage(Index(1)), AuthorisationType.ACE)
      .setValue(AuthorisationReferenceNumberPage(Index(0)), "List item 2")

    val result = new AddAnotherAuthorisationViewModelProvider()(userAnswers)
    result.listItems.length mustBe 2
  }

}
