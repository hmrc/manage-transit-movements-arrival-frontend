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

package controllers.identification

import base.{AppWithDefaultMockFixtures, SpecBase}
import controllers.identification.{routes => identificationRoutes}
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.identification.CheckIdentificationAnswersViewModel
import viewModels.identification.CheckIdentificationAnswersViewModel.CheckIdentificationAnswersViewModelProvider
import viewModels.sections.Section
import views.html.identification.CheckIdentificationAnswersView

class CheckIdentificationAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModelProvider = mock[CheckIdentificationAnswersViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CheckIdentificationAnswersViewModelProvider].toInstance(mockViewModelProvider))

  "Check Identification Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val sampleSections = arbitrary[Seq[Section]].sample.value

      when(mockViewModelProvider.apply(any(), any())(any()))
        .thenReturn(CheckIdentificationAnswersViewModel(sampleSections))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, identificationRoutes.CheckIdentificationAnswersController.onPageLoad(mrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[CheckIdentificationAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(mrn, sampleSections)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, identificationRoutes.CheckIdentificationAnswersController.onPageLoad(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to MRN Page" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, identificationRoutes.CheckIdentificationAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.identification.routes.MovementReferenceNumberController.onPageLoad().url
    }
  }
}
