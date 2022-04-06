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
import forms.EoriNumberFormProvider
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.{ConsigneeEoriNumberPage, ConsigneeNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.EoriNumberView

import scala.concurrent.Future

class ConsigneeEoriNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new EoriNumberFormProvider()
  private val form         = formProvider(traderName)
  private val mode         = NormalMode

  lazy val eoriNumberRoute = routes.ConsigneeEoriNumberController.onPageLoad(mrn, mode).url

  "EoriNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(ConsigneeNamePage, consigneeName)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, eoriNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[EoriNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode, consigneeName)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(ConsigneeNamePage, traderName)
        .setValue(ConsigneeEoriNumberPage, eoriNumber.value)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, eoriNumberRoute)

      val result = route(app, request).value

      val filledForm = formProvider(traderName).bind(Map("value" -> eoriNumber.value))

      val view = injector.instanceOf[EoriNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode, traderName)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      val userAnswers = emptyUserAnswers
        .setValue(ConsigneeNamePage, traderName)

      setExistingUserAnswers(userAnswers)

      val request =
        FakeRequest(POST, eoriNumberRoute)
          .withFormUrlEncodedBody(("value", eoriNumber.value))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(ConsigneeNamePage, traderName)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, eoriNumberRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[EoriNumberView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, traderName)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, eoriNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, eoriNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
