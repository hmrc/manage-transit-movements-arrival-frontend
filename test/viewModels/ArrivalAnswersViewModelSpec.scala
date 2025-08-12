/*
 * Copyright 2023 HM Revenue & Customs
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

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import viewModels.ArrivalAnswersViewModel.ArrivalAnswersViewModelProvider
import viewModels.identification.IdentificationAnswersViewModel

class ArrivalAnswersViewModelSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  val identificationProvider = new IdentificationAnswersViewModel.IdentificationAnswersViewModelProvider()
  val locationProvider       = new LocationOfGoodsAnswersViewModel.LocationOfGoodsAnswersViewModelProvider()

  "must return 2 sections" in {
    forAll(arbitraryArrivalAnswers(emptyUserAnswers)) {
      userAnswers =>
        val viewModelProvider = new ArrivalAnswersViewModel.ArrivalAnswersViewModelProvider(
          identificationProvider,
          locationProvider
        )
        val sections = viewModelProvider.apply(userAnswers).sections
        sections.size mustEqual 2
    }
  }
}
