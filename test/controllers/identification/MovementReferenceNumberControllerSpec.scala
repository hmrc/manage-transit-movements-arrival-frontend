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

package controllers.identification

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.CacheConnector.IsTransitionalStateException
import forms.identification.MovementReferenceNumberFormProvider
import models.{ArrivalMessage, CheckMode, MovementReferenceNumber, NormalMode, SubmissionStatus, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.*
import org.scalacheck.Gen
import play.api.data.{Form, FormError}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.{SessionService, SubmissionService}
import views.html.identification.MovementReferenceNumberView

import java.time.LocalDateTime
import scala.concurrent.Future

class MovementReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val mockSubmissionService = mock[SubmissionService]
  private val mockSessionService    = mock[SessionService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind(classOf[SubmissionService]).toInstance(mockSubmissionService),
        bind(classOf[SessionService]).toInstance(mockSessionService)
      )

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSubmissionService)
    reset(mockSessionService)
    when(mockSessionService.set(any(), any())(any())).thenCallRealMethod()
  }

  val formProvider                        = new MovementReferenceNumberFormProvider()
  val form: Form[MovementReferenceNumber] = formProvider()

  private lazy val movementReferenceNumberRoute = routes.MovementReferenceNumberController.onPageLoad().url

  "MovementReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {
      when(mockSessionService.get(any()))
        .thenReturn(None)

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, movementReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[MovementReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {
      "when onPageLoad" in {
        when(mockSessionService.get(any()))
          .thenReturn(Some(mrn.toString))

        setNoExistingUserAnswers()

        lazy val movementReferenceNumberRoute = routes.MovementReferenceNumberController.onPageLoad().url

        val request = FakeRequest(GET, movementReferenceNumberRoute)

        val result = route(app, request).value

        val filledForm = form.bind(Map("value" -> mrn.toString))

        val view = injector.instanceOf[MovementReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm)(request, messages).toString
      }

      "when onPageReload" in {
        setNoExistingUserAnswers()

        lazy val movementReferenceNumberRoute = routes.MovementReferenceNumberController.onPageReload(mrn).url

        val request = FakeRequest(GET, movementReferenceNumberRoute)

        val result = route(app, request).value

        val filledForm = form.bind(Map("value" -> mrn.toString))

        val view = injector.instanceOf[MovementReferenceNumberView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(filledForm)(request, messages).toString
      }
    }

    "must redirect to the next page with NormalMode" - {
      "when valid data is submitted" - {
        "and user answers not found in session repository" in {
          when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(None) `thenReturn` Future.successful(Some(emptyUserAnswers))
          when(mockSessionRepository.put(any())(any())) `thenReturn` Future.successful(true)

          setNoExistingUserAnswers()

          val request = FakeRequest(POST, movementReferenceNumberRoute)
            .withFormUrlEncodedBody(("value", mrn.toString))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual
            controllers.identification.routes.DestinationOfficeController.onPageLoad(mrn, NormalMode).url

          verify(mockSessionRepository, times(2)).get(eqTo(mrn.toString))(any())
          verify(mockSessionRepository).put(eqTo(mrn.toString))(any())
          verify(mockSessionService).set(any(), eqTo(mrn))(any())
        }
      }
    }

    "must redirect to the next page with CheckMode " - {
      "when valid data is submitted" - {
        "and user answers found in session repository" - {
          "and answers have previously been submitted and rejected" in {
            val now = LocalDateTime.now()

            val arrivalMessages = Seq(
              ArrivalMessage("IE057", now),
              ArrivalMessage("IE007", now.minusDays(1))
            )

            when(mockSubmissionService.getMessages(eqTo(mrn))(any()))
              .thenReturn(Future.successful(arrivalMessages))

            val userAnswers = emptyUserAnswers.copy(submissionStatus = SubmissionStatus.Submitted)
            when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(Some(userAnswers))

            setNoExistingUserAnswers()

            val request = FakeRequest(POST, movementReferenceNumberRoute)
              .withFormUrlEncodedBody(("value", mrn.toString))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual
              controllers.identification.routes.DestinationOfficeController.onPageLoad(mrn, CheckMode).url

            verify(mockSessionRepository).get(eqTo(mrn.toString))(any())
            verify(mockSessionRepository, never()).put(any())(any())
            verify(mockSessionService).set(any(), eqTo(mrn))(any())

            val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
            verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
            userAnswersCaptor.getValue mustBe userAnswers.copy(submissionStatus = SubmissionStatus.Amending)
          }

          "and answers have not previously been submitted" in {
            forAll(Gen.oneOf(SubmissionStatus.NotSubmitted, SubmissionStatus.Amending)) {
              submissionStatus =>
                beforeEach()

                val userAnswers = emptyUserAnswers.copy(submissionStatus = submissionStatus)
                when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(Some(userAnswers))

                setNoExistingUserAnswers()

                val request = FakeRequest(POST, movementReferenceNumberRoute)
                  .withFormUrlEncodedBody(("value", mrn.toString))

                val result = route(app, request).value

                status(result) mustEqual SEE_OTHER
                redirectLocation(result).value mustEqual
                  controllers.identification.routes.DestinationOfficeController.onPageLoad(mrn, CheckMode).url

                verify(mockSessionRepository).get(eqTo(mrn.toString))(any())
                verify(mockSessionRepository, never()).put(any())(any())
                verify(mockSessionRepository, never()).set(any())(any())
                verify(mockSessionService).set(any(), eqTo(mrn))(any())
            }
          }
        }
      }
    }

    "must return a Bad Request and errors" - {

      "when invalid data is submitted" in {

        setExistingUserAnswers(emptyUserAnswers)

        val invalidAnswer = ""

        val request = FakeRequest(POST, movementReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", invalidAnswer))

        val filledForm = form.bind(Map("value" -> invalidAnswer))

        val result = route(app, request).value

        val view = injector.instanceOf[MovementReferenceNumberView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(filledForm)(request, messages).toString
      }

      "when answers have previously been submitted" in {
        val now = LocalDateTime.now()

        val arrivalMessages = Seq(
          ArrivalMessage("IE007", now)
        )

        when(mockSubmissionService.getMessages(eqTo(mrn))(any()))
          .thenReturn(Future.successful(arrivalMessages))

        val userAnswers = emptyUserAnswers.copy(submissionStatus = SubmissionStatus.Submitted)
        when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(Some(userAnswers))

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, movementReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", mrn.toString))

        val boundForm = form
          .withError(
            FormError(
              "value",
              "This Movement Reference Number has already been used for a submitted arrival notification. Enter a unique MRN if you want to start a new arrival notification."
            )
          )

        val result = route(app, request).value

        val view = injector.instanceOf[MovementReferenceNumberView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm)(request, messages).toString

        verify(mockSessionRepository).get(eqTo(mrn.toString))(any())
        verify(mockSessionRepository, never()).put(any())(any())
        verify(mockSessionRepository, never()).set(any())(any())
      }

      "and answers have not previously been submitted" in {
        forAll(Gen.oneOf(SubmissionStatus.NotSubmitted, SubmissionStatus.Amending)) {
          submissionStatus =>
            beforeEach()

            val userAnswers = emptyUserAnswers.copy(submissionStatus = submissionStatus)
            when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(Some(userAnswers))

            setNoExistingUserAnswers()

            val request = FakeRequest(POST, movementReferenceNumberRoute)
              .withFormUrlEncodedBody(("value", mrn.toString))

            val result = route(app, request).value

            status(result) mustEqual SEE_OTHER
            redirectLocation(result).value mustEqual
              controllers.identification.routes.DestinationOfficeController.onPageLoad(mrn, CheckMode).url

            verify(mockSessionRepository).get(eqTo(mrn.toString))(any())
            verify(mockSessionRepository, never()).put(any())(any())
            verify(mockSessionRepository, never()).set(any())(any())
            verify(mockSessionService).set(any(), eqTo(mrn))(any())
        }
      }
    }

    "must redirect to 'draft no longer available' for a IsTransitionalStateException exception" in {
      val mrn = "01YH1DI5N73MAQI1Y8"

      when(mockSessionRepository.get(any())(any()))
        .thenReturn(Future.failed(new IsTransitionalStateException(mrn)))

      val request = FakeRequest(POST, movementReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", mrn))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.routes.DraftNoLongerAvailableController.onPageLoad().url

      verify(mockSessionRepository, times(1)).get(eqTo(mrn))(any())
      verify(mockSessionRepository, never()).put(any())(any())
    }
  }
}
