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

import base.SpecBase
import generators.Generators
import models.NormalMode
import models.identification.authorisation.AuthorisationType
import pages.identification.authorisation._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import viewModels.sections.AuthorisationSection

class AuthorisationSectionSpec extends SpecBase with Generators {

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(AuthorisationTypePage(eventIndex), AuthorisationType.Option2)
      .setValue(AuthorisationReferenceNumberPage(eventIndex), "AuthRefNo")

    val section = new AuthorisationSection().apply(userAnswers, NormalMode, eventIndex)

    section.sectionTitle mustNot be(defined)
    section.rows.size mustBe 2
    section.rows.head.value.content mustBe Text("option2")
    section.rows(1).value.content mustBe Text("AuthRefNo")
  }
}
