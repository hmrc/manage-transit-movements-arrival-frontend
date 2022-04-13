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
import generators.Generators
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode}
import org.scalacheck.Arbitrary.arbitrary
import pages.events._
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class EventInfoSectionSpec extends SpecBase with Generators {

  private val genCountryList = arbitrary[Seq[Country]].sample.value
  private val countryList    = CountryList(genCountryList)

  "must return section" - {

    "when goods moved to different vehicle/container" in {
      val userAnswers = emptyUserAnswers
        .setValue(EventCountryPage(eventIndex), CountryCode("code"))
        .setValue(EventPlacePage(eventIndex), "place")
        .setValue(EventReportedPage(eventIndex), true)
        .setValue(IsTranshipmentPage(eventIndex), true)

      val section = new EventInfoSection().apply(userAnswers, NormalMode, eventIndex, isTranshipment = true, countryList)

      section.sectionTitle mustNot be(defined)
      section.rows.size mustBe 3
      section.rows.head.value.content mustBe Text("code")
      section.rows(1).value.content mustBe Text("place")
      section.rows(2).value.content mustBe Text("Yes")
    }

    "when goods not moved to different vehicle/container" in {
      val userAnswers = emptyUserAnswers
        .setValue(EventCountryPage(eventIndex), CountryCode("code"))
        .setValue(EventPlacePage(eventIndex), "place")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), false)
        .setValue(IncidentInformationPage(eventIndex), "information")

      val section = new EventInfoSection().apply(userAnswers, NormalMode, eventIndex, isTranshipment = false, countryList)

      section.sectionTitle mustNot be(defined)
      section.rows.size mustBe 5
      section.rows.head.value.content mustBe Text("code")
      section.rows(1).value.content mustBe Text("place")
      section.rows(2).value.content mustBe Text("No")
      section.rows(3).value.content mustBe Text("No")
      section.rows(4).value.content mustBe Text("information")
    }
  }
}
