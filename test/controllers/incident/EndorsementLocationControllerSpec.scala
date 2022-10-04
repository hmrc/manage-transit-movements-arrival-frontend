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

package controllers.incident

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.incident.EndorsementLocationFormProvider
import generators.Generators
import models.NormalMode
import models.reference.Country
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.incident.{EndorsementCountryPage, EndorsementLocationPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.EndorsementLocationView

import scala.concurrent.Future

class EndorsementLocationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country                       = arbitrary[Country].sample.value
  private val formProvider                  = new EndorsementLocationFormProvider()
  private val form                          = formProvider("incident.endorsementLocation", country.description)
  private val mode                          = NormalMode
  private lazy val endorsementLocationRoute = routes.EndorsementLocationController.onPageLoad(mrn, mode, index).url

  "EndorsementLocation Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue(EndorsementCountryPage(index), country)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, endorsementLocationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[EndorsementLocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, country.description, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(EndorsementCountryPage(index), country)
        .setValue(EndorsementLocationPage(index), "test string")
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, endorsementLocationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "test string"))

      val view = injector.instanceOf[EndorsementLocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, country.description, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(EndorsementCountryPage(index), country)

      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, endorsementLocationRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(EndorsementCountryPage(index), country)

      setExistingUserAnswers(userAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, endorsementLocationRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[EndorsementLocationView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, country.description, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, endorsementLocationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, endorsementLocationRoute)
        .withFormUrlEncodedBody(("value", "test string"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
