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
import forms.identification.MovementReferenceNumberFormProvider
import models.{MovementReferenceNumber, SubmissionStatus, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{never, times, verify, when}
import org.scalacheck.Gen
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.identification.MovementReferenceNumberView

import scala.concurrent.Future

class MovementReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  val formProvider                        = new MovementReferenceNumberFormProvider()
  val form: Form[MovementReferenceNumber] = formProvider()

  private lazy val movementReferenceNumberRoute = routes.MovementReferenceNumberController.onPageLoad().url

  "MovementReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, movementReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[MovementReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
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

    "must redirect to the next page with NormalMode when valid data is submitted and user answers not found in session repository" in {

      when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(None) `thenReturn` Future.successful(Some(emptyUserAnswers))
      when(mockSessionRepository.put(any())(any())) `thenReturn` Future.successful(true)

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, movementReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", mrn.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual
        s"/manage-transit-movements/arrivals/$mrn/identification/office-of-destination"

      verify(mockSessionRepository, times(2)).get(eqTo(mrn.toString))(any())
      verify(mockSessionRepository).put(eqTo(mrn.toString))(any())
    }

    "must redirect to the next page with CheckMode when valid data is submitted and user answers found in session repository" - {

      "and answers have previously been submitted" in {
        val userAnswers = emptyUserAnswers.copy(submissionStatus = SubmissionStatus.Submitted)
        when(mockSessionRepository.get(any())(any())) `thenReturn` Future.successful(Some(userAnswers))

        setNoExistingUserAnswers()

        val request = FakeRequest(POST, movementReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", mrn.toString))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual
          s"/manage-transit-movements/arrivals/$mrn/identification/change-office-of-destination"

        verify(mockSessionRepository).get(eqTo(mrn.toString))(any())
        verify(mockSessionRepository, never()).put(any())(any())

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
              s"/manage-transit-movements/arrivals/$mrn/identification/change-office-of-destination"

            verify(mockSessionRepository).get(eqTo(mrn.toString))(any())
            verify(mockSessionRepository, never()).put(any())(any())
            verify(mockSessionRepository, never()).set(any())(any())
        }
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

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
  }
}
