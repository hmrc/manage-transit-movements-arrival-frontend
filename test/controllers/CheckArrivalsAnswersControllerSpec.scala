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
import generators.Generators
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, verifyNoInteractions, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.{SessionService, SubmissionService}
import viewModels.ArrivalAnswersViewModel
import viewModels.ArrivalAnswersViewModel.ArrivalAnswersViewModelProvider
import viewModels.sections.Section
import views.html.CheckArrivalsAnswersView

class CheckArrivalsAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private lazy val mockViewModelProvider               = mock[ArrivalAnswersViewModelProvider]
  private val mockSubmissionService: SubmissionService = mock[SubmissionService]
  private val mockSessionService: SessionService       = mock[SessionService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[ArrivalAnswersViewModelProvider].toInstance(mockViewModelProvider),
        bind(classOf[SubmissionService]).toInstance(mockSubmissionService),
        bind(classOf[SessionService]).toInstance(mockSessionService)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
    reset(mockSubmissionService)
    reset(mockSessionService)

    when(mockSessionService.remove(any())(any())).thenCallRealMethod()
  }

  private val userAnswersGen = arbitraryArrivalAnswers(emptyUserAnswers)

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

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }

    "must redirect to Declaration Submitted when submission succeeds" in {
      forAll(userAnswersGen) {
        userAnswers =>
          beforeEach()

          setExistingUserAnswers(userAnswers)

          when(mockSubmissionService.post(any())(any()))
            .thenReturn(response(OK))

          val request = FakeRequest(POST, routes.CheckArrivalsAnswersController.onSubmit(mrn).url)

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.DeclarationSubmittedController.onPageLoad(mrn).url

          verify(mockSessionService).remove(any())(any())
      }
    }

    "must redirect to technical difficulties when submission fails" in {
      forAll(arbitraryArrivalAnswers(emptyUserAnswers), Gen.choose(400: Int, 599: Int)) {
        (userAnswers, errorCode) =>
          beforeEach()

          setExistingUserAnswers(userAnswers)

          when(mockSubmissionService.post(any())(any()))
            .thenReturn(response(errorCode))

          val request = FakeRequest(POST, routes.CheckArrivalsAnswersController.onSubmit(mrn).url)

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.ErrorController.technicalDifficulties().url

          verify(mockSessionService).remove(any())(any())
      }
    }

    "must redirect to unanswered page when answers incomplete" in {
      setExistingUserAnswers(emptyUserAnswers)

      when(mockSubmissionService.post(any())(any()))
        .thenReturn(response(OK))

      val request = FakeRequest(POST, routes.CheckArrivalsAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.identification.routes.DestinationOfficeController.onPageLoad(mrn, NormalMode).url

      verifyNoInteractions(mockSessionService)
    }
  }
}
