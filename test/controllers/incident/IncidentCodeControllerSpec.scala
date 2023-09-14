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

package controllers.incident

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.Constants.incidentCodesList
import forms.EnumerableFormProvider
import generators.Generators
import models.NormalMode
import models.incident.IncidentCode
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import pages.incident.IncidentCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.IncidentCodeView
import org.scalacheck.Arbitrary.arbitrary
import services.IncidentCodeService

import scala.concurrent.Future

class IncidentCodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val ic1                                          = arbitrary[IncidentCode].sample.value
  private val ic2                                          = arbitrary[IncidentCode].sample.value
  private val ics                                          = Seq(ic1, ic2)
  private val formProvider                                 = new EnumerableFormProvider()
  private val form                                         = formProvider[IncidentCode]("incident.incidentCode", ics)
  private val mode                                         = NormalMode
  private lazy val incidentCodeRoute                       = routes.IncidentCodeController.onPageLoad(mrn, mode, index).url
  private val mockIncidentCodeService: IncidentCodeService = mock[IncidentCodeService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))
      .overrides(bind(classOf[IncidentCodeService]).toInstance(mockIncidentCodeService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIncidentCodeService)
    when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(ics))
  }
  "IncidentCode Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, ics, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(IncidentCodePage(index), ic1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> incidentCodesList.head.toString))

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, ics, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, incidentCodeRoute)
        .withFormUrlEncodedBody(("value", ic1.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, incidentCodeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, ics, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, incidentCodeRoute)
        .withFormUrlEncodedBody(("value", incidentCodesList.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
