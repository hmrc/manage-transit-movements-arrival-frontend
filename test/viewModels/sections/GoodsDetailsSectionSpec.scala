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
import models.GoodsLocation
import models.reference.CustomsOffice
import pages.{AuthorisedLocationPage, CustomsOfficePage, CustomsSubPlacePage, GoodsLocationPage}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class GoodsDetailsSectionSpec extends SpecBase {

  "must return section" - {

    "when goods are at authorised consignee's location" in {

      val userAnswers = emptyUserAnswers
        .setValue(GoodsLocationPage, GoodsLocation.AuthorisedConsigneesLocation)
        .setValue(AuthorisedLocationPage, "location")

      val section = new GoodsDetailsSection().apply(userAnswers)

      section.sectionTitle.get mustBe "Goods details"
      section.rows.size mustBe 2
      section.rows.head.value.content mustBe Text("Authorised consignee’s location (simplified)")
      section.rows(1).value.content mustBe Text("location")
    }

    "when goods are at customs-approved location" in {

      val userAnswers = emptyUserAnswers
        .setValue(GoodsLocationPage, GoodsLocation.BorderForceOffice)
        .setValue(CustomsSubPlacePage, "place")
        .setValue(CustomsOfficePage, CustomsOffice("office", None, None))

      val section = new GoodsDetailsSection().apply(userAnswers)

      section.sectionTitle.get mustBe "Goods details"
      section.rows.size mustBe 3
      section.rows.head.value.content mustBe Text("Customs-approved location (normal)")
      section.rows(1).value.content mustBe Text("place")
      section.rows(2).value.content mustBe Text("office")
    }
  }
}
