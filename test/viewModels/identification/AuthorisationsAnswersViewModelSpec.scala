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
import models.identification.authorisation.AuthorisationType
import models.{Index, Mode}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import viewModels.Link
import viewModels.identification.AuthorisationsAnswersViewModel.AuthorisationsAnswersViewModelProvider

class AuthorisationsAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  "must return sections" in {
    forAll(arbitrary[Mode], Gen.choose(1, frontendAppConfig.maxIdentificationAuthorisations)) {
      (mode, numberOfAuthorisations) =>
        val userAnswers = (0 until numberOfAuthorisations).foldLeft(emptyUserAnswers) {
          (acc, i) =>
            acc
              .setValue(AuthorisationTypePage(Index(i)), arbitrary[AuthorisationType].sample.value)
              .setValue(AuthorisationReferenceNumberPage(Index(i)), arbitrary[String].sample.value)
        }

        val viewModelProvider = injector.instanceOf[AuthorisationsAnswersViewModelProvider]
        val section           = viewModelProvider.apply(userAnswers, mode).section

        section.sectionTitle.get mustBe "Authorisations"
        section.rows.size mustBe numberOfAuthorisations
        section.addAnotherLink.get mustBe Link(
          "add-or-remove-authorisations",
          "Add or remove authorisations",
          controllers.identification.authorisation.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.mrn, mode).url
        )
    }
  }
}
