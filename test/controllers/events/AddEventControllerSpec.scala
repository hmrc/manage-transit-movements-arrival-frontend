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
import forms.events.AddEventFormProvider
import models.{Index, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.events.{AddEventPage, EventPlacePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import views.html.events.AddEventView

import scala.concurrent.Future

class AddEventControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                   = new AddEventFormProvider()
  private def form(allowMoreEvents: Boolean) = formProvider(allowMoreEvents)

  private val mode = NormalMode

  private lazy val addEventRoute = routes.AddEventController.onPageLoad(mrn, mode).url

  private val range = 0 to 2

  private val maxedUserAnswers = range.foldLeft(emptyUserAnswers) {
    (acc, i) =>
      acc.setValue(EventPlacePage(Index(i)), s"$i")
  }

  private lazy val maxedListItems = range.map {
    i =>
      ListItem(
        name = s"$i",
        changeUrl = routes.CheckEventAnswersController.onPageLoad(mrn, Index(i)).url,
        removeUrl = routes.ConfirmRemoveEventController.onPageLoad(mrn, Index(i), mode).url
      )
  }

  "AddEvent Controller" - {

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreEvents = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addEventRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddEventView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreEvents), mrn, mode, _ => Nil, allowMoreEvents)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreEvents = false

        setExistingUserAnswers(maxedUserAnswers)

        val request = FakeRequest(GET, addEventRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddEventView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreEvents), mrn, mode, _ => maxedListItems, allowMoreEvents)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {
      "when max limit not reached" in {

        val allowMoreEvents = true

        val userAnswers = emptyUserAnswers.setValue(AddEventPage, true)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addEventRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddEventView]

        status(result) mustEqual OK

        val filledForm = form(allowMoreEvents).bind(Map("value" -> "true"))

        contentAsString(result) mustEqual
          view(filledForm, mrn, mode, _ => Nil, allowMoreEvents)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreEvents = false

        val userAnswers = maxedUserAnswers.setValue(AddEventPage, true)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addEventRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddEventView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreEvents), mrn, mode, _ => maxedListItems, allowMoreEvents)(request, messages).toString
      }
    }

    "must redirect to the next page" - {
      "when valid data is submitted and max limit not reached" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addEventRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }

      "when max limit reached" in {

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        setExistingUserAnswers(maxedUserAnswers)

        val request = FakeRequest(POST, addEventRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {

        val allowMoreEvents = true

        setExistingUserAnswers(emptyUserAnswers)

        val request   = FakeRequest(POST, addEventRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form(allowMoreEvents).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddEventView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, mode, _ => Nil, allowMoreEvents)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addEventRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addEventRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
