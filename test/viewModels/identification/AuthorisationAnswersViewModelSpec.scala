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
import models.NormalMode
import models.identification.authorisation.AuthorisationType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}

class AuthorisationAnswersViewModelSpec extends SpecBase with Generators {

  "must return section" in {
    val userAnswers = emptyUserAnswers
      .setValue(AuthorisationTypePage(eventIndex), arbitrary[AuthorisationType].sample.value)
      .setValue(AuthorisationReferenceNumberPage(eventIndex), Gen.alphaNumStr.sample.value)

    val section = AuthorisationAnswersViewModel.apply(userAnswers, authorisationIndex, NormalMode).section

    section.sectionTitle mustNot be(defined)
    section.rows.size mustBe 2
  }
}
