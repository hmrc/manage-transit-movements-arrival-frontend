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
import models.GoodsLocation
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, verifyNoInteractions, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import pages.GoodsLocationPage
import viewModels.sections._

class CheckYourAnswersViewModelSpec extends SpecBase with Generators with ViewModelGenerators with BeforeAndAfterEach {

  private val mockMrnSection: MrnSection                                               = mock[MrnSection]
  private val mockGoodsDetailsSection: GoodsDetailsSection                             = mock[GoodsDetailsSection]
  private val mockTraderDetailsSection: TraderDetailsSection                           = mock[TraderDetailsSection]
  private val mockConsigneeDetailsSection: ConsigneeDetailsSection                     = mock[ConsigneeDetailsSection]
  private val mockPlaceOfNotificationDetailsSection: PlaceOfNotificationDetailsSection = mock[PlaceOfNotificationDetailsSection]
  private val mockEventsSection: EventsSection                                         = mock[EventsSection]

  private val sampleMrnSection                        = arbitrary[Section].sample.value
  private val sampleGoodsDetailsSection               = arbitrary[Section].sample.value
  private val sampleTraderDetailsSection              = arbitrary[Section].sample.value
  private val sampleConsigneeDetailsSection           = arbitrary[Section].sample.value
  private val samplePlaceOfNotificationDetailsSection = arbitrary[Section].sample.value
  private val sampleEventsSection                     = arbitrary[Section].sample.value

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(
      mockMrnSection,
      mockGoodsDetailsSection,
      mockTraderDetailsSection,
      mockConsigneeDetailsSection,
      mockPlaceOfNotificationDetailsSection,
      mockEventsSection
    )

    when(mockMrnSection.apply(any())(any())).thenReturn(sampleMrnSection)
    when(mockGoodsDetailsSection.apply(any())(any())).thenReturn(sampleGoodsDetailsSection)
    when(mockTraderDetailsSection.apply(any())(any())).thenReturn(sampleTraderDetailsSection)
    when(mockConsigneeDetailsSection.apply(any())(any())).thenReturn(sampleConsigneeDetailsSection)
    when(mockPlaceOfNotificationDetailsSection.apply(any())(any())).thenReturn(samplePlaceOfNotificationDetailsSection)
    when(mockEventsSection.apply(any())(any())).thenReturn(sampleEventsSection)
  }

  "must return sections" - {
    "when goods are at authorised consignee's location" in {

      val userAnswers = emptyUserAnswers.setValue(GoodsLocationPage, GoodsLocation.AuthorisedConsigneesLocation)

      val viewModel = new CheckYourAnswersViewModel(
        mockMrnSection,
        mockGoodsDetailsSection,
        mockTraderDetailsSection,
        mockConsigneeDetailsSection,
        mockPlaceOfNotificationDetailsSection,
        mockEventsSection
      )

      val sections = viewModel.apply(userAnswers)

      sections.size mustBe 4

      sections.head mustBe sampleMrnSection
      sections(1) mustBe sampleGoodsDetailsSection
      sections(2) mustBe sampleConsigneeDetailsSection
      sections(3) mustBe sampleEventsSection

      verify(mockMrnSection).apply(eqTo(userAnswers))(any())
      verify(mockGoodsDetailsSection).apply(eqTo(userAnswers))(any())
      verify(mockConsigneeDetailsSection).apply(eqTo(userAnswers))(any())
      verify(mockEventsSection).apply(eqTo(userAnswers))(any())

      verifyNoInteractions(mockTraderDetailsSection, mockPlaceOfNotificationDetailsSection)
    }

    "when goods are at customs-approved location" in {

      val userAnswers = emptyUserAnswers.setValue(GoodsLocationPage, GoodsLocation.BorderForceOffice)

      val viewModel = new CheckYourAnswersViewModel(
        mockMrnSection,
        mockGoodsDetailsSection,
        mockTraderDetailsSection,
        mockConsigneeDetailsSection,
        mockPlaceOfNotificationDetailsSection,
        mockEventsSection
      )

      val sections = viewModel.apply(userAnswers)

      sections.size mustBe 5

      sections.head mustBe sampleMrnSection
      sections(1) mustBe sampleGoodsDetailsSection
      sections(2) mustBe sampleTraderDetailsSection
      sections(3) mustBe samplePlaceOfNotificationDetailsSection
      sections(4) mustBe sampleEventsSection

      verify(mockMrnSection).apply(eqTo(userAnswers))(any())
      verify(mockGoodsDetailsSection).apply(eqTo(userAnswers))(any())
      verify(mockTraderDetailsSection).apply(eqTo(userAnswers))(any())
      verify(mockPlaceOfNotificationDetailsSection).apply(eqTo(userAnswers))(any())
      verify(mockEventsSection).apply(eqTo(userAnswers))(any())

      verifyNoInteractions(mockConsigneeDetailsSection)
    }
  }
}
