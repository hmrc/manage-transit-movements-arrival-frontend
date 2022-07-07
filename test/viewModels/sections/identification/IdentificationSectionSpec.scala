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

package viewModels.sections.identification

import java.time.LocalDate

import base.SpecBase
import generators.Generators
import models.NormalMode
import pages.identification._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class IdentificationSectionSpec extends SpecBase with Generators {

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(ArrivalDatePage, LocalDate.of(2020: Int, 12: Int, 31: Int)) //"31 December 2020")
      .setValue(IdentificationNumberPage, "IdNo")
      .setValue(IsSimplifiedProcedurePage, true)

    val section = new IdentificationSection().apply(userAnswers, NormalMode)

    section.sectionTitle mustNot be(defined)
    section.rows.size mustBe 3
    section.rows.head.value.content mustBe Text("2020-12-31")
    section.rows(1).value.content mustBe Text("Yes")
    section.rows(2).value.content mustBe Text("IdNo")
  }
}
