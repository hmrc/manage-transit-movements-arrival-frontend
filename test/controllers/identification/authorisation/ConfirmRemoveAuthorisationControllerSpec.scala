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
import models.{Index, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.identification.authorisation._
import play.api.test.FakeRequest
import play.api.test.Helpers._
import queries.AuthorisationQuery
import views.html.ConcurrentRemoveErrorView
import views.html.identification.authorisation.ConfirmRemoveAuthorisationView

import scala.concurrent.Future

class ConfirmRemoveAuthorisationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val prefix                  = "identification.authorisation.confirmRemoveAuthorisation"
  private val authTitle               = "AuthRefNo"
  private val formProvider            = new ConfirmRemoveItemFormProvider()
  private val form                    = formProvider(prefix, authTitle)
  private val mode                    = NormalMode
  private lazy val confirmRemoveRoute = routes.ConfirmRemoveAuthorisationController.onPageLoad(mrn, authorisationIndex, mode).url
  private val userAnswersWithAuthRef  = emptyUserAnswers.setValue(AuthorisationReferenceNumberPage(authorisationIndex), authTitle)

  "ConfirmRemoveAuthorisation Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(userAnswersWithAuthRef)

      val request = FakeRequest(GET, confirmRemoveRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

      val view = injector.instanceOf[ConfirmRemoveAuthorisationView]

      contentAsString(result) mustEqual
        view(form, mrn, authorisationIndex, mode, authTitle)(request, messages).toString
    }

    "must return error page when user tries to remove an authorisation that does not exists" in {

      val updatedAnswer = userAnswersWithAuthRef.removeValue(AuthorisationQuery(authorisationIndex))
      setExistingUserAnswers(updatedAnswer)

      val request = FakeRequest(GET, confirmRemoveRoute)

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND

      val view = injector.instanceOf[ConcurrentRemoveErrorView]

      contentAsString(result) mustEqual
        view(mrn, "noAuthorisation", onwardRoute.url, "authorisation")(request, messages).toString

      status(result) mustEqual NOT_FOUND
    }

    "must return error page when there are multiple authorisations and user tries to remove the last authorisation that is already removed" in {

      val routeWithLastIndex = routes.ConfirmRemoveAuthorisationController.onPageLoad(mrn, Index(2), NormalMode).url
      val updatedAnswer      = userAnswersWithAuthRef.setValue(AuthorisationReferenceNumberPage(Index(1)), "AuthRefNo-1")

      setExistingUserAnswers(updatedAnswer)

      val request = FakeRequest(GET, routeWithLastIndex)

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND

      val view = injector.instanceOf[ConcurrentRemoveErrorView]

      contentAsString(result) mustEqual
        view(mrn, "multipleAuthorisation", onwardRoute.url, "authorisation")(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and call to remove authorisation" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(userAnswersWithAuthRef)

      val request =
        FakeRequest(POST, confirmRemoveRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val uaRemoveAuthorisation = UserAnswers(
        userAnswersWithAuthRef.mrn,
        userAnswersWithAuthRef.eoriNumber,
        userAnswersWithAuthRef.removeValue(AuthorisationQuery(authorisationIndex)).data,
        userAnswersWithAuthRef.lastUpdated,
        id = userAnswersWithAuthRef.id
      )

      verify(mockSessionRepository, times(1)).set(uaRemoveAuthorisation)
    }

    "must redirect to the next page when valid data is submitted and call to remove authorisation is false" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(userAnswersWithAuthRef)

      val request =
        FakeRequest(POST, confirmRemoveRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val uaRemoveAuthorisation = UserAnswers(
        userAnswersWithAuthRef.mrn,
        userAnswersWithAuthRef.eoriNumber,
        userAnswersWithAuthRef.removeValue(AuthorisationQuery(authorisationIndex)).data,
        userAnswersWithAuthRef.lastUpdated
      )

      verify(mockSessionRepository, times(0)).set(uaRemoveAuthorisation)
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(userAnswersWithAuthRef)

      val request   = FakeRequest(POST, confirmRemoveRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveAuthorisationView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, authorisationIndex, mode, authTitle)(request, messages).toString
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

      val request =
        FakeRequest(POST, confirmRemoveRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
