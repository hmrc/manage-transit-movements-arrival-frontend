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

package viewModels

import base.SpecBase
import generators.MessagesModelGenerators
import models.TranshipmentType._
import models.domain.{ContainerDomain, SealDomain}
import models.reference.{Country, CountryCode}
import models.{CheckMode, CountryList, Index}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.IncidentOnRoutePage
import pages.events._
import pages.events.seals.{HaveSealsChangedPage, SealIdentityPage}
import pages.events.transhipments._

class CheckEventAnswersViewModelSpec extends SpecBase with ScalaCheckPropertyChecks with MessagesModelGenerators {

  private val genCountryList = arbitrary[Seq[Country]].sample.value
  private val countryList    = CountryList(genCountryList)

  "when event is an incident" - {
    "and hasn't been reported and did not move to different vehicle/container and no seals changed" in {

      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), false)
        .setValue(IncidentInformationPage(eventIndex), "value")
        .setValue(HaveSealsChangedPage(eventIndex), false)

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)

      sections.head.sectionTitle must not be defined
      sections.length mustEqual 2
      sections.head.rows.length mustEqual 5
    }

    "and has been reported, did not move to different vehicle/container and no seals changed" in {
      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), false)
        .setValue(IncidentInformationPage(eventIndex), "value")
        .setValue(HaveSealsChangedPage(eventIndex), false)

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)

      sections.head.sectionTitle must not be defined
      sections.head.rows.length mustEqual 5
    }

    "and has been reported, did not move to different vehicle/container and seals changed" in {
      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), false)
        .setValue(IncidentInformationPage(eventIndex), "value")
        .setValue(HaveSealsChangedPage(eventIndex), true)
        .setValue(SealIdentityPage(eventIndex, Index(0)), SealDomain("seal1"))
        .setValue(SealIdentityPage(eventIndex, Index(1)), SealDomain("seal2"))

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)

      sections.head.sectionTitle must not be defined
      sections.head.rows.length mustEqual 5
    }

    "and has been reported and did not move to different vehicle/container show the event info only" in {
      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), true)
        .setValue(IsTranshipmentPage(eventIndex), false)
        .setValue(HaveSealsChangedPage(eventIndex), false)

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)

      sections.head.sectionTitle must not be defined
      sections.head.rows.length mustEqual 4
    }
  }

  "when event is a transhipment" - {
    "and the goods have moved to different vehicle display event info and vehicle info sections" in {
      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), true)
        .setValue(TranshipmentTypePage(eventIndex), DifferentVehicle)
        .setValue(TransportIdentityPage(eventIndex), "value")
        .setValue(TransportNationalityPage(eventIndex), CountryCode("GB"))
        .setValue(HaveSealsChangedPage(eventIndex), false)

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)

      sections.head.sectionTitle must not be defined
      sections.head.rows.length mustEqual 3
      sections(1).sectionTitle must be(defined)
      sections(1).rows.length mustEqual 4
    }

    "and the goods have moved to different container display event info and container info sections" in {
      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), true)
        .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
        .setValue(ContainerNumberPage(eventIndex, Index(0)), ContainerDomain("value"))
        .setValue(ContainerNumberPage(eventIndex, Index(1)), ContainerDomain("value"))
        .setValue(ContainerNumberPage(eventIndex, Index(2)), ContainerDomain("value"))
        .setValue(HaveSealsChangedPage(eventIndex), false)

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)
      sections.head.sectionTitle must not be defined
      sections.head.rows.length mustEqual 3
      sections(1).sectionTitle must be(defined)
      sections(1).rows.length mustEqual 2
    }

    "and the goods have moved to both different containers and vehicles  display event info and vehicle and containers info sections" in {
      val ua = emptyUserAnswers
        .setValue(IncidentOnRoutePage, true)
        .setValue(EventCountryPage(eventIndex), CountryCode("GB"))
        .setValue(EventPlacePage(eventIndex), "value")
        .setValue(EventReportedPage(eventIndex), false)
        .setValue(IsTranshipmentPage(eventIndex), true)
        .setValue(TranshipmentTypePage(eventIndex), DifferentContainerAndVehicle)
        .setValue(ContainerNumberPage(eventIndex, Index(0)), ContainerDomain("value"))
        .setValue(ContainerNumberPage(eventIndex, Index(1)), ContainerDomain("value"))
        .setValue(ContainerNumberPage(eventIndex, Index(2)), ContainerDomain("value"))
        .setValue(TransportIdentityPage(eventIndex), "value")
        .setValue(TransportNationalityPage(eventIndex), CountryCode("GB"))
        .setValue(HaveSealsChangedPage(eventIndex), false)

      val sections = new CheckEventAnswersViewModel().apply(ua, eventIndex, CheckMode, countryList)

      sections.length mustEqual 5
      sections.head.sectionTitle must not be defined
      sections(1).sectionTitle must be(defined)
      sections(1).rows.length mustEqual 2
      sections(2).sectionTitle must be(defined)
      sections(2).rows.length mustEqual 3

    }

  }

}
