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

package controllers.events

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.events.EventPlaceFormProvider
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.events.EventPlacePage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.events.EventPlaceView

import scala.concurrent.Future

class EventPlaceControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider: EventPlaceFormProvider = new EventPlaceFormProvider()
  private val form: Form[String]                   = formProvider()
  private val mode                                 = NormalMode
  private lazy val eventPlaceRoute: String         = controllers.events.routes.EventPlaceController.onPageLoad(mrn, eventIndex, mode).url

  "EventPlace Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, eventPlaceRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[EventPlaceView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode, eventIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(EventPlacePage(eventIndex), "answer").success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, eventPlaceRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> "answer"))

      val view = injector.instanceOf[EventPlaceView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode, eventIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, eventPlaceRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, eventPlaceRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[EventPlaceView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, eventIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, eventPlaceRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, eventPlaceRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
