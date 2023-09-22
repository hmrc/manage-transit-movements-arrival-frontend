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

package controllers.incident.transportMeans

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.EnumerableFormProvider
import models.NormalMode
import models.incident.transportMeans.Identification
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.transportMeans.IdentificationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.transportMeans.IdentificationView

import scala.concurrent.Future

class IdentificationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val id1                      = Identification("U", "UN/LOCODE")
  private val id2                      = Identification("W", "GPS coordinates")
  private val id3                      = Identification("Z", "Free text")
  private val ids                      = Seq(id1, id2, id3)
  private val formProvider             = new EnumerableFormProvider()
  private val form                     = formProvider[Identification]("incident.transportMeans.identification", ids)
  private val mode                     = NormalMode
  private lazy val identificationRoute = routes.IdentificationController.onPageLoad(mrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))

  "Identification Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, identificationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[IdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, ids, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(IdentificationPage(index), id1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, identificationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> ids.head.toString))

      val view = injector.instanceOf[IdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, ids, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, identificationRoute)
        .withFormUrlEncodedBody(("value", id1.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, identificationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[IdentificationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, ids, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, identificationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, identificationRoute)
        .withFormUrlEncodedBody(("value", id1.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
