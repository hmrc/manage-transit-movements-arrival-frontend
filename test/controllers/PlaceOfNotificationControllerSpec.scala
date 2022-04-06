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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.PlaceOfNotificationFormProvider
import matchers.JsonMatchers
import models.NormalMode
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.PlaceOfNotificationPage
import play.api.libs.json.{JsObject, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import views.html.PlaceOfNotificationView

import scala.concurrent.Future

class PlaceOfNotificationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new PlaceOfNotificationFormProvider()
  private val form         = formProvider()
  private val mode         = NormalMode

  private lazy val placeOfNotificationRoute = routes.PlaceOfNotificationController.onPageLoad(mrn, mode).url

  "PlaceOfNotification Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, placeOfNotificationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[PlaceOfNotificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(form, mrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(PlaceOfNotificationPage, "answer").success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, placeOfNotificationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "answer"))

      val view = injector.instanceOf[PlaceOfNotificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual view(filledForm, mrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, placeOfNotificationRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, placeOfNotificationRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[PlaceOfNotificationView]

      contentAsString(result) mustEqual view(boundForm, mrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, placeOfNotificationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, placeOfNotificationRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
