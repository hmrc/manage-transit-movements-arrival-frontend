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

package controllers.identification.authorisation

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.ConfirmRemoveItemFormProvider
import models.UserAnswers
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalacheck.Gen
import pages.identification.authorisation._
import pages.sections.AuthorisationSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.identification.authorisation.ConfirmRemoveAuthorisationView

import scala.concurrent.Future

class ConfirmRemoveAuthorisationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val prefix                  = "identification.authorisation.confirmRemoveAuthorisation"
  private val authTitle               = Gen.alphaNumStr.sample.value
  private val formProvider            = new ConfirmRemoveItemFormProvider()
  private val form                    = formProvider(prefix, authTitle)
  private lazy val confirmRemoveRoute = routes.ConfirmRemoveAuthorisationController.onPageLoad(mrn, authorisationIndex).url
  private val baseAnswers             = emptyUserAnswers.setValue(AuthorisationReferenceNumberPage(authorisationIndex), authTitle)

  "ConfirmRemoveAuthorisation Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(baseAnswers)

      val request = FakeRequest(GET, confirmRemoveRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

      val view = injector.instanceOf[ConfirmRemoveAuthorisationView]

      contentAsString(result) mustEqual
        view(form, mrn, authorisationIndex, authTitle)(request, messages).toString
    }

    "must return error page when user tries to remove an authorisation that does not exist" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to the next page when valid data is submitted and call to remove authorisation" in {
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(baseAnswers)

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.identification.authorisation.routes.AddAnotherAuthorisationController.onPageLoad(baseAnswers.mrn).url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())
      userAnswersCaptor.getValue.get(AuthorisationSection(authorisationIndex)) mustNot be(defined)
    }

    "must redirect to the next page when valid data is submitted and call to remove authorisation is false" in {
      setExistingUserAnswers(baseAnswers)

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.identification.authorisation.routes.AddAnotherAuthorisationController.onPageLoad(baseAnswers.mrn).url

      verify(mockSessionRepository, never()).set(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(baseAnswers)

      val request   = FakeRequest(POST, confirmRemoveRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveAuthorisationView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, authorisationIndex, authTitle)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
