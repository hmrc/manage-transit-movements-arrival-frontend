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
import pages.events.transhipments.{TransportIdentityPage, TransportNationalityPage}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text

class VehicleInformationSectionSpec extends SpecBase with Generators {

  private val genCountryList = arbitrary[Seq[Country]].sample.value
  private val countryList    = CountryList(genCountryList)

  "must return section" in {

    val userAnswers = emptyUserAnswers
      .setValue(TransportIdentityPage(eventIndex), "identity")
      .setValue(TransportNationalityPage(eventIndex), CountryCode("code"))

    val sections = new VehicleInformationSection().apply(userAnswers, NormalMode, eventIndex, countryList)

    sections.sectionTitle.get mustBe "Vehicle information"
    sections.rows.size mustBe 2
    sections.rows.head.value.content mustBe Text("identity")
    sections.rows(1).value.content mustBe Text("code")
  }
}
