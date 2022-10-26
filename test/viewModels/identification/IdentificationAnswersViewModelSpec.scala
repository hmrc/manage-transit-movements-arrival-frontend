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
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.Mode
import org.scalacheck.Arbitrary.arbitrary
import viewModels.identification.IdentificationAnswersViewModel.IdentificationAnswersViewModelProvider

class IdentificationAnswersViewModelSpec extends SpecBase with Generators with ArrivalUserAnswersGenerator {

  "must return section" in {
    forAll(arbitrary[Mode], arbitraryIdentificationAnswers(emptyUserAnswers)) {
      (mode, answers) =>
        val section = new IdentificationAnswersViewModelProvider().apply(answers, mode).section

        section.sectionTitle.get mustBe "Identification"
        section.rows.size mustBe 4
    }
  }
}
