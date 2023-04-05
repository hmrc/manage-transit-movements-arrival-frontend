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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.SubmissionConnector
import generators.Generators
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.ArrivalAnswersViewModel
import viewModels.ArrivalAnswersViewModel.ArrivalAnswersViewModelProvider
import viewModels.sections.Section
import views.html.CheckArrivalsAnswersView

class CheckArrivalsAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModelProvider                   = mock[ArrivalAnswersViewModelProvider]
  private val mockSubmissionConnector: SubmissionConnector = mock[SubmissionConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[ArrivalAnswersViewModelProvider].toInstance(mockViewModelProvider))
      .overrides(bind(classOf[SubmissionConnector]).toInstance(mockSubmissionConnector))

  "Check your Answers Controller" - {

    "must return OK and the correct view for a GET" in {
      val sampleSections = arbitrary[List[Section]].sample.value
      when(mockViewModelProvider.apply(any())(any())).thenReturn(ArrivalAnswersViewModel(sampleSections))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.CheckArrivalsAnswersController.onPageLoad(mrn).url)

      val result = route(app, request).value

      val view = injector.instanceOf[CheckArrivalsAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(mrn, sampleSections)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.CheckArrivalsAnswersController.onPageLoad(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Declaration Submitted Controller" in {
      setExistingUserAnswers(emptyUserAnswers)

      when(mockSubmissionConnector.post(any())(any()))
        .thenReturn(response(OK))

      val request = FakeRequest(POST, routes.CheckArrivalsAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.DeclarationSubmittedController.onPageLoad(mrn).url

    }

  }
}
