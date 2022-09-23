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
import forms.CountryFormProvider
import generators.Generators
import models.{CountryList, NormalMode}
import navigation.Navigator
import navigation.annotations.IdentificationDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.IncidentCountryPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.incident.IncidentCountryView

import scala.concurrent.Future

class IncidentCountryControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country1    = arbitraryCountry.arbitrary.sample.get
  private val country2    = arbitraryCountry.arbitrary.sample.get
  private val countryList = CountryList(Seq(country1, country2))

  private val formProvider = new CountryFormProvider()
  private val form         = formProvider("incident.incidentCountry", countryList)
  private val mode         = NormalMode

  private val mockCountriesService: CountriesService = mock[CountriesService]
  private lazy val incidentCountryRoute              = routes.IncidentCountryController.onPageLoad(mrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[IdentificationDetails]).toInstance(fakeNavigator))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "IncidentCountry Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, incidentCountryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, countryList.countries, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))
      val userAnswers = emptyUserAnswers.setValue(IncidentCountryPage, country1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, incidentCountryRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> country1.code.code))

      val view = injector.instanceOf[IncidentCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, countryList.countries, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, incidentCountryRoute)
          .withFormUrlEncodedBody(("value", country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, incidentCountryRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[IncidentCountryView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, countryList.countries, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, incidentCountryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, incidentCountryRoute)
        .withFormUrlEncodedBody(("value", country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}