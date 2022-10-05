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
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.identification._
import pages.identification.authorisation._
import viewModels.Link

import java.time.LocalDate

class CheckIdentificationAnswersViewModelSpec extends SpecBase with Generators {

  "must return sections" in {
    val userAnswers = emptyUserAnswers
      .setValue(ArrivalDatePage, arbitrary[LocalDate].sample.value)
      .setValue(IsSimplifiedProcedurePage, ProcedureType.Simplified)
      .setValue(AuthorisationTypePage(authorisationIndex), arbitrary[AuthorisationType].sample.value)
      .setValue(AuthorisationReferenceNumberPage(authorisationIndex), Gen.alphaNumStr.sample.value)

    val sections = CheckIdentificationAnswersViewModel.apply(userAnswers, NormalMode).sections

    sections.size mustBe 2

    sections.head.sectionTitle mustNot be(defined)
    sections.head.rows.size mustBe 3
    sections.head.addAnotherLink mustNot be(defined)

    sections(1).sectionTitle.get mustBe "Authorisations"
    sections(1).rows.size mustBe 1
    sections(1).addAnotherLink.get mustBe Link(
      id = "add-or-remove",
      text = "Add or remove authorisations",
      href = controllers.identification.authorisation.routes.AddAnotherAuthorisationController.onPageLoad(userAnswers.mrn).url
    )
  }
}
