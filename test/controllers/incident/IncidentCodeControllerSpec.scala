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
import forms.incident.IncidentCodeFormProvider
import generators.Generators
import models.{IncidentCodeList, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.IncidentCodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.IncidentCodeService
import views.html.incident.IncidentCodeView

import scala.concurrent.Future

class IncidentCodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val incidentCode1    = arbitraryIncidentCode.arbitrary.sample.get
  private val incidentCode2    = arbitraryIncidentCode.arbitrary.sample.get
  private val incidentCodeList = IncidentCodeList(Seq(incidentCode1, incidentCode2))

  private val formProvider = new IncidentCodeFormProvider()
  private val form         = formProvider("incident.incidentCode", incidentCodeList)
  private val mode         = NormalMode

  private val mockIncidentCodeService: IncidentCodeService = mock[IncidentCodeService]
  private lazy val incidentCodeRoute                       = routes.IncidentCodeController.onPageLoad(mrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentCodeService]).toInstance(mockIncidentCodeService))

  "IncidentCode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(incidentCodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, incidentCodeList.incidentCodes, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(incidentCodeList))
      val userAnswers = emptyUserAnswers.setValue(IncidentCodePage(index), incidentCode1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, incidentCodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> incidentCode1.code))

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, incidentCodeList.incidentCodes, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(incidentCodeList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, incidentCodeRoute)
          .withFormUrlEncodedBody(("value", incidentCode1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockIncidentCodeService.getIncidentCodes()(any())).thenReturn(Future.successful(incidentCodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, incidentCodeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCodeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, incidentCodeList.incidentCodes, mode, index)(request, messages).toString
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
        .withFormUrlEncodedBody(("value", incidentCode1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
