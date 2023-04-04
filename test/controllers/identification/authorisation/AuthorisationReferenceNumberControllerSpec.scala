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

package controllers.identification.authorisation

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.identification.AuthorisationRefNoFormProvider
import models.NormalMode
import models.identification.authorisation.AuthorisationType
import navigation.AuthorisationNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.identification.authorisation.{AuthorisationReferenceNumberPage, AuthorisationTypePage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.identification.authorisation.AuthorisationReferenceNumberView

import scala.concurrent.Future

class AuthorisationReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val authorisationType                      = AuthorisationType.ACT
  private val formProvider                           = new AuthorisationRefNoFormProvider()
  private val form                                   = formProvider("identification.authorisation.authorisationReferenceNumber", authorisationType.toString)
  private val mode                                   = NormalMode
  private lazy val authorisationReferenceNumberRoute = routes.AuthorisationReferenceNumberController.onPageLoad(mrn, index, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AuthorisationNavigatorProvider]).toInstance(fakeAuthorisationNavigatorProvider))

  "AuthorisationReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(AuthorisationTypePage(index), AuthorisationType.ACT)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, authorisationReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[AuthorisationReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, index, AuthorisationType.ACT.toString, mode)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationTypePage(index), AuthorisationType.ACT)
        .setValue(AuthorisationReferenceNumberPage(index), "testString")

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, authorisationReferenceNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "testString"))

      val view = injector.instanceOf[AuthorisationReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, index, AuthorisationType.ACT.toString, mode)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationTypePage(index), AuthorisationType.ACT)

      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, authorisationReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", "testString"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(AuthorisationTypePage(index), AuthorisationType.ACT)

      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, authorisationReferenceNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AuthorisationReferenceNumberView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, index, AuthorisationType.ACT.toString, mode)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, authorisationReferenceNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, authorisationReferenceNumberRoute)
          .withFormUrlEncodedBody(("value", "testString"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
