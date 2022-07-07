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

package viewModels.identification

import base.SpecBase
import generators.{Generators, ViewModelGenerators}
import models.CheckMode
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.BeforeAndAfterEach
import viewModels.sections.identification.IdentificationSection
import viewModels.sections.Section

class CheckIdentificationAnswersViewModelSpec extends SpecBase with Generators with ViewModelGenerators with BeforeAndAfterEach {

  private val mockIdentification: IdentificationSection = mock[IdentificationSection]

  private val sampleIdentification = arbitrary[Section].sample.value

  private val mode = CheckMode

  override def beforeEach(): Unit = {
    super.beforeEach()

    reset(mockIdentification)

    when(mockIdentification.apply(any(), any())(any()))
      .thenReturn(sampleIdentification)
  }

  "must return sections" in {
    val userAnswers = emptyUserAnswers

    val viewModel = new CheckIdentificationAnswersViewModel(mockIdentification)

    val sections = viewModel.apply(userAnswers, mode)

    sections.size mustBe 1

    sections.head mustBe sampleIdentification

    verify(mockIdentification).apply(eqTo(userAnswers), eqTo(mode))(any())
  }
}
