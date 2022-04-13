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
import generators.{Generators, ViewModelGenerators}
import models.reference.Country
import models.{CheckMode, CountryList}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import pages.events._
import viewModels.sections.{EventInfoSection, EventTypeSection, SealsSection, Section}

class CheckEventAnswersViewModelSpec extends SpecBase with Generators with ViewModelGenerators with BeforeAndAfterEach {

  private val genCountryList = arbitrary[Seq[Country]].sample.value
  private val countryList    = CountryList(genCountryList)

  private val mockEventInfoSection: EventInfoSection = mock[EventInfoSection]
  private val mockEventTypeSection: EventTypeSection = mock[EventTypeSection]
  private val mockSealSection: SealsSection          = mock[SealsSection]

  private val sampleEventInfoSection  = arbitrary[Section].sample.value
  private val sampleEventTypeSections = arbitrary[Seq[Section]].sample.value
  private val sampleSealSection       = arbitrary[Section].sample.value

  private val mode = CheckMode

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(mockEventInfoSection, mockEventTypeSection, mockSealSection)

    when(mockEventInfoSection.apply(any(), any(), any(), any(), any())(any()))
      .thenReturn(sampleEventInfoSection)

    when(mockEventTypeSection.apply(any(), any(), any(), any(), any())(any()))
      .thenReturn(sampleEventTypeSections)

    when(mockSealSection.apply(any(), any(), any())(any()))
      .thenReturn(sampleSealSection)
  }

  "must return sections" - {
    "when 'is transhipment' is true" in {

      val userAnswers = emptyUserAnswers.setValue(IsTranshipmentPage(eventIndex), true)

      val viewModel = new CheckEventAnswersViewModel(mockEventInfoSection, mockEventTypeSection, mockSealSection)

      val sections = viewModel.apply(userAnswers, eventIndex, mode, countryList)

      sections.size mustBe sampleEventTypeSections.size + 2

      sections.head mustBe sampleEventInfoSection
      sections.slice(1, sampleEventTypeSections.size + 1) mustBe sampleEventTypeSections
      sections.last mustBe sampleSealSection

      verify(mockEventInfoSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(true), eqTo(countryList))(any())

      verify(mockEventTypeSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(true), eqTo(countryList))(any())

      verify(mockSealSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex))(any())
    }

    "when 'is transhipment' is false" in {

      val userAnswers = emptyUserAnswers.setValue(IsTranshipmentPage(eventIndex), false)

      val viewModel = new CheckEventAnswersViewModel(mockEventInfoSection, mockEventTypeSection, mockSealSection)

      val sections = viewModel.apply(userAnswers, eventIndex, mode, countryList)

      sections.size mustBe sampleEventTypeSections.size + 2

      sections.head mustBe sampleEventInfoSection
      sections.slice(1, sampleEventTypeSections.size + 1) mustBe sampleEventTypeSections
      sections.last mustBe sampleSealSection

      verify(mockEventInfoSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(false), eqTo(countryList))(any())

      verify(mockEventTypeSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(false), eqTo(countryList))(any())

      verify(mockSealSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex))(any())
    }

    "when 'is transhipment' is undefined" in {

      val userAnswers = emptyUserAnswers

      val viewModel = new CheckEventAnswersViewModel(mockEventInfoSection, mockEventTypeSection, mockSealSection)

      val sections = viewModel.apply(userAnswers, eventIndex, mode, countryList)

      sections.size mustBe sampleEventTypeSections.size + 2

      sections.head mustBe sampleEventInfoSection
      sections.slice(1, sampleEventTypeSections.size + 1) mustBe sampleEventTypeSections
      sections.last mustBe sampleSealSection

      verify(mockEventInfoSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(false), eqTo(countryList))(any())

      verify(mockEventTypeSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(false), eqTo(countryList))(any())

      verify(mockSealSection).apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex))(any())
    }
  }
}
