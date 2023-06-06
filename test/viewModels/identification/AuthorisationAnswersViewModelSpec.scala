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
import models.identification.authorisation.AuthorisationType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import viewModels.identification.AuthorisationAnswersViewModel.AuthorisationAnswersViewModelProvider

class AuthorisationAnswersViewModelSpec extends SpecBase with Generators {

  "authorisation section" - {
    "must have 2 rows" in {
      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationTypePage, arbitrary[AuthorisationType].sample.value)
        .setValue(AuthorisationReferenceNumberPage, Gen.alphaNumStr.sample.value)

      forAll(userAnswers, arbitrary[Mode]) {
        (answers, mode) =>
          val section = new AuthorisationAnswersViewModelProvider().apply(answers, mode).section
          section.sectionTitle mustBe "Authorisations"
          section.rows.size mustBe 2
          section.addAnotherLink must not be defined
      }
    }
  }
}
