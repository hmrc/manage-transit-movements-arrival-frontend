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

package viewModels.identification

import base.SpecBase
import generators.Generators
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import pages.identification.AuthorisationReferenceNumberPage
import viewModels.identification.IdentificationAnswersViewModel.IdentificationAnswersViewModelProvider

class IdentificationAnswersViewModelSpec extends SpecBase with Generators {

  "identification section" - {
    "must have 5 rows" in {
      forAll(arbitrary[Mode], arbitraryIdentificationAnswers(emptyUserAnswers), arbitrary[String]) {
        (mode, answers, refNum) =>
          val userAnswers = answers.setValue(AuthorisationReferenceNumberPage, refNum)
          val section     = new IdentificationAnswersViewModelProvider().apply(userAnswers, mode).section

          section.sectionTitle must not be defined
          section.rows.size mustEqual 5
          section.addAnotherLink must not be defined
      }
    }
  }
}
