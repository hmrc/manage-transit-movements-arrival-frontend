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
import forms.events.EventCountryFormProvider
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.events.EventCountryPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.events.EventCountryView

import scala.concurrent.Future

class EventCountryControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                   = new EventCountryFormProvider()
  private val country                        = Country(CountryCode("GB"), "United Kingdom")
  val countries                              = CountryList(Vector(country))
  private val form                           = formProvider(countries)
  private lazy val mockCountriesService      = mock[CountriesService]
  private val mode                           = NormalMode
  private lazy val eventCountryRoute: String = routes.EventCountryController.onPageLoad(mrn, eventIndex, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CountriesService].toInstance(mockCountriesService))

  "EventCountry Controller" - {

    "must return OK and the correct view for a GET" in {
      setExistingUserAnswers(emptyUserAnswers)
      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countries))

      val request = FakeRequest(GET, eventCountryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[EventCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countries.countries, mrn, mode, eventIndex)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = UserAnswers(mrn, eoriNumber).set(EventCountryPage(eventIndex), country.code).success.value
      val filledForm  = form.bind(Map("value" -> "GB"))

      setExistingUserAnswers(userAnswers)
      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countries))

      val request = FakeRequest(GET, eventCountryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[EventCountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, countries.countries, mrn, mode, eventIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countries))

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, eventCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getTransitCountries()(any())).thenReturn(Future.successful(countries))

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, eventCountryRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      val view = injector.instanceOf[EventCountryView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countries.countries, mrn, mode, eventIndex)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, eventCountryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, eventCountryRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }

}
