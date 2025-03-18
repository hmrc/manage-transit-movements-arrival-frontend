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
import forms.SelectableFormProvider.NationalityFormProvider
import generators.Generators
import models.{NormalMode, SelectableList}
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.transportMeans.TransportNationalityPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.NationalitiesService
import views.html.incident.transportMeans.TransportNationalityView

import scala.concurrent.Future

class TransportNationalityControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val nationality1    = arbitraryNationality.arbitrary.sample.get
  private val nationality2    = arbitraryNationality.arbitrary.sample.get
  private val nationalityList = SelectableList(Seq(nationality1, nationality2))

  private val formProvider = new NationalityFormProvider()
  private val form         = formProvider("incident.transportMeans.transportNationality", nationalityList)
  private val field        = formProvider.field
  private val mode         = NormalMode

  private val mockNationalitiesService: NationalitiesService = mock[NationalitiesService]
  private lazy val transportNationalityRoute                 = routes.TransportNationalityController.onPageLoad(mrn, mode, incidentIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))
      .overrides(bind(classOf[NationalitiesService]).toInstance(mockNationalitiesService))

  "TransportNationality Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockNationalitiesService.getNationalities()(any())).thenReturn(Future.successful(nationalityList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, transportNationalityRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TransportNationalityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, nationalityList.values, mode, incidentIndex)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockNationalitiesService.getNationalities()(any())).thenReturn(Future.successful(nationalityList))
      val userAnswers = emptyUserAnswers.setValue(TransportNationalityPage(incidentIndex), nationality1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, transportNationalityRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map(field -> nationality1.code))

      val view = injector.instanceOf[TransportNationalityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, nationalityList.values, mode, incidentIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockNationalitiesService.getNationalities()(any())).thenReturn(Future.successful(nationalityList))
      when(mockSessionRepository.set(any())(any())) `thenReturn` Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, transportNationalityRoute)
        .withFormUrlEncodedBody((field, nationality1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockNationalitiesService.getNationalities()(any())).thenReturn(Future.successful(nationalityList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, transportNationalityRoute).withFormUrlEncodedBody((field, "invalid value"))
      val boundForm = form.bind(Map(field -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[TransportNationalityView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, nationalityList.values, mode, incidentIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, transportNationalityRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, transportNationalityRoute)
        .withFormUrlEncodedBody((field, nationality1.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }
  }
}
