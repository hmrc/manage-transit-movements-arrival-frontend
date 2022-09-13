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

package controllers.locationOfGoods

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.NameFormProvider
import models.NormalMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.locationOfGoods.AdditionalIdentifierPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.locationOfGoods.AdditionalIdentifierView

import scala.concurrent.Future

class AdditionalIdentifierControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                   = new NameFormProvider()
  private val form                           = formProvider("locationOfGoods.additionalIdentifier")
  private val mode                           = NormalMode
  private lazy val additionalidentifierRoute = routes.AdditionalIdentifierController.onPageLoad(mrn, mode).url

  "Additionalidentifier Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, additionalidentifierRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[AdditionalIdentifierView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(AdditionalIdentifierPage, "test string")
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, additionalidentifierRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "test string"))

      val view = injector.instanceOf[AdditionalIdentifierView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, additionalidentifierRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, additionalidentifierRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AdditionalIdentifierView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, additionalidentifierRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, additionalidentifierRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
