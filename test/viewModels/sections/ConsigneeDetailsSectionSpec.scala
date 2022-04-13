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
import models.reference.CustomsOffice
import pages.{ConsigneeAddressPage, ConsigneeEoriNumberPage, ConsigneeNamePage, SimplifiedCustomsOfficePage}
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}

class ConsigneeDetailsSectionSpec extends SpecBase {

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(ConsigneeNamePage, "name")
      .setValue(ConsigneeEoriNumberPage, "eori")
      .setValue(ConsigneeAddressPage, consigneeAddress)
      .setValue(SimplifiedCustomsOfficePage, CustomsOffice("office", None, None))

    val section = new ConsigneeDetailsSection().apply(userAnswers)

    section.sectionTitle.get mustBe "Consignee’s details"
    section.rows.size mustBe 4
    section.rows.head.value.content mustBe Text("name")
    section.rows(1).value.content mustBe Text("eori")
    section.rows(2).value.content mustBe HtmlContent(Html("buildingAndStreet<br>city<br>NE99 1XN"))
    section.rows(3).value.content mustBe Text("office")
  }
}
