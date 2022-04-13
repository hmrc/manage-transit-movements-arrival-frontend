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

package viewModels.sections

import base.SpecBase
import pages.{TraderAddressPage, TraderEoriPage, TraderNamePage}
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}

class TraderDetailsSectionSpec extends SpecBase {

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(TraderNamePage, "name")
      .setValue(TraderEoriPage, "eori")
      .setValue(TraderAddressPage, traderAddress)

    val section = new TraderDetailsSection().apply(userAnswers)

    section.sectionTitle.get mustBe "Trader’s details"
    section.rows.size mustBe 3
    section.rows.head.value.content mustBe Text("name")
    section.rows(1).value.content mustBe Text("eori")
    section.rows(2).value.content mustBe HtmlContent(Html("buildingAndStreet<br>city<br>NE99 1XN"))
  }
}
