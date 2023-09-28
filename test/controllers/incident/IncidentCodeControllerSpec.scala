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
import forms.EnumerableFormProvider
import generators.Generators
import models.NormalMode
import models.reference.IncidentCode
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.incident.IncidentCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ReferenceDataDynamicRadioService
import views.html.incident.IncidentCodeView

import scala.concurrent.Future

class IncidentCodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val ics = arbitrary[Seq[IncidentCode]].sample.value
  private val ic  = ics.head

  private val formProvider                                              = new EnumerableFormProvider()
  private val form                                                      = formProvider[IncidentCode]("incident.incidentCode", ics)
  private val mode                                                      = NormalMode
  private lazy val incidentCodeRoute                                    = routes.IncidentCodeController.onPageLoad(mrn, mode, index).url
  private val mockIncidentCodeService: ReferenceDataDynamicRadioService = mock[ReferenceDataDynamicRadioService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))
      .overrides(bind(classOf[ReferenceDataDynamicRadioService]).toInstance(mockIncidentCodeService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockIncidentCodeService)
    when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(ics))
  }

  "IncidentCode Controller" - {

    "must return OK and the correct view for a GET" in {
      when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(ics))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, ics, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(ics))

      val userAnswers = emptyUserAnswers.setValue(IncidentCodePage(index), ics.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> ic.code))

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, ics, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, incidentCodeRoute)
        .withFormUrlEncodedBody(("value", ic.code))

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
        .withFormUrlEncodedBody(("value", ic.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
