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
import pages.{IsTraderAddressPlaceOfNotificationPage, PlaceOfNotificationPage}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class PlaceOfNotificationDetailsSectionSpec extends SpecBase {

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(IsTraderAddressPlaceOfNotificationPage, false)
      .setValue(PlaceOfNotificationPage, "place")

    val section = new PlaceOfNotificationDetailsSection().apply(userAnswers)

    section.sectionTitle.get mustBe "Place where this was completed"
    section.rows.size mustBe 2
    section.rows.head.value.content mustBe Text("No")
    section.rows(1).value.content mustBe Text("place")
  }
}
