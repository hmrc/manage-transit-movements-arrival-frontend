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
import generators.{Generators, ViewModelGenerators}
import models.reference.Country
import models.{CheckMode, CountryList, TranshipmentType}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, verifyNoInteractions, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import pages.events.transhipments.TranshipmentTypePage

class EventTypeSectionSpec extends SpecBase with Generators with ViewModelGenerators with BeforeAndAfterEach {

  private val genCountryList = arbitrary[Seq[Country]].sample.value
  private val countryList    = CountryList(genCountryList)

  private val isTranshipment = arbitrary[Boolean].sample.value

  private val mockVehicleInformationSection: VehicleInformationSection = mock[VehicleInformationSection]
  private val mockDifferentContainerSection: DifferentContainerSection = mock[DifferentContainerSection]
  private val mockDifferentVehicleSection: DifferentVehicleSection     = mock[DifferentVehicleSection]

  private val sampleVehicleInformationSection  = arbitrary[Section].sample.value
  private val sampleDifferentContainerSections = arbitrary[Seq[Section]].sample.value
  private val sampleDifferentVehicleSection    = arbitrary[Section].sample.value

  private val mode = CheckMode

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(mockVehicleInformationSection, mockDifferentContainerSection, mockDifferentVehicleSection)

    when(mockVehicleInformationSection.apply(any(), any(), any(), any())(any()))
      .thenReturn(sampleVehicleInformationSection)

    when(mockDifferentContainerSection.apply(any(), any(), any(), any())(any()))
      .thenReturn(sampleDifferentContainerSections)

    when(mockDifferentVehicleSection.apply(any(), any(), any(), any(), any())(any()))
      .thenReturn(sampleDifferentVehicleSection)
  }

  "must return sections" - {
    "when transhipment type is DifferentVehicle" in {

      val userAnswers = emptyUserAnswers.setValue(TranshipmentTypePage(eventIndex), TranshipmentType.DifferentVehicle)

      val viewModel = new EventTypeSection(mockVehicleInformationSection, mockDifferentContainerSection, mockDifferentVehicleSection)

      val sections = viewModel.apply(userAnswers, mode, eventIndex, isTranshipment, countryList)

      sections.size mustBe 1

      sections.head mustBe sampleDifferentVehicleSection

      verify(mockDifferentVehicleSection)
        .apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(isTranshipment), eqTo(countryList))(any())

      verifyNoInteractions(mockVehicleInformationSection, mockDifferentContainerSection)
    }

    "when transhipment type is DifferentContainer" in {

      val userAnswers = emptyUserAnswers.setValue(TranshipmentTypePage(eventIndex), TranshipmentType.DifferentContainer)

      val viewModel = new EventTypeSection(mockVehicleInformationSection, mockDifferentContainerSection, mockDifferentVehicleSection)

      val sections = viewModel.apply(userAnswers, mode, eventIndex, isTranshipment, countryList)

      sections.size mustBe sampleDifferentContainerSections.size

      sections mustBe sampleDifferentContainerSections

      verify(mockDifferentContainerSection)
        .apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo("checkEventAnswers.section.title.differentContainer"))(any())

      verifyNoInteractions(mockVehicleInformationSection, mockDifferentVehicleSection)
    }

    "when transhipment type is DifferentContainerAndVehicle" in {

      val userAnswers = emptyUserAnswers.setValue(TranshipmentTypePage(eventIndex), TranshipmentType.DifferentContainerAndVehicle)

      val viewModel = new EventTypeSection(mockVehicleInformationSection, mockDifferentContainerSection, mockDifferentVehicleSection)

      val sections = viewModel.apply(userAnswers, mode, eventIndex, isTranshipment, countryList)

      sections.size mustBe sampleDifferentContainerSections.size + 1

      sections.dropRight(1) mustBe sampleDifferentContainerSections
      sections.last mustBe sampleVehicleInformationSection

      verify(mockDifferentContainerSection)
        .apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo("checkEventAnswers.section.title.differentContainerAndVehicle"))(any())

      verify(mockVehicleInformationSection)
        .apply(eqTo(userAnswers), eqTo(mode), eqTo(eventIndex), eqTo(countryList))(any())

      verifyNoInteractions(mockDifferentVehicleSection)
    }
  }
}
