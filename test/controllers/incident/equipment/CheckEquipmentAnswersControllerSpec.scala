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

package controllers.incident.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import generators.Generators
import models.NormalMode
import navigation.EquipmentsNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.incident.EquipmentAnswersViewModel
import viewModels.incident.EquipmentAnswersViewModel.EquipmentAnswersViewModelProvider
import viewModels.sections.Section
import views.html.incident.equipment.CheckEquipmentAnswersView

class CheckEquipmentAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModelProvider = mock[EquipmentAnswersViewModelProvider]

  private val mode = NormalMode

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[EquipmentAnswersViewModelProvider].toInstance(mockViewModelProvider))
      .overrides(bind(classOf[EquipmentsNavigatorProvider]).toInstance(fakeEquipmentsNavigatorProvider))

  "Check Equipment Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val sampleSections = arbitrary[List[Section]].sample.value

      when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
        .thenReturn(EquipmentAnswersViewModel(sampleSections))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.CheckEquipmentAnswersController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex).url)

      val result = route(app, request).value

      val view = injector.instanceOf[CheckEquipmentAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(mrn, mode, incidentIndex, equipmentIndex, sampleSections)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.CheckEquipmentAnswersController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to next page" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, routes.CheckEquipmentAnswersController.onSubmit(mrn, mode, incidentIndex, equipmentIndex).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }
  }
}
