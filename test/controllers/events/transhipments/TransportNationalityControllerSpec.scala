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

package controllers.events.transhipments

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.events.transhipments.TransportNationalityFormProvider
import models.reference.{Country, CountryCode}
import models.{CountryList, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.events.transhipments.TransportNationalityPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.events.transhipments.TransportNationalityView

import scala.concurrent.Future

class TransportNationalityControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                           = new TransportNationalityFormProvider()
  private val country                                = Country(CountryCode("GB"), "United Kingdom")
  private val countries                              = CountryList(Seq(country))
  private val form                                   = formProvider(countries)
  private val mode                                   = NormalMode
  private lazy val mockCountriesService              = mock[CountriesService]
  private lazy val transportNationalityRoute: String = routes.TransportNationalityController.onPageLoad(mrn, eventIndex, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CountriesService].toInstance(mockCountriesService))

  "TransportNationality Controller" - {

    "must return OK and the correct view for a GET" in {
      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countries))

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, transportNationalityRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TransportNationalityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countries.countries, mrn, eventIndex, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countries))
      val filledForm  = form.bind(Map("value" -> "GB"))
      val userAnswers = UserAnswers(mrn, eoriNumber).set(TransportNationalityPage(eventIndex), country.code).success.value

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, transportNationalityRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TransportNationalityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, countries.countries, mrn, eventIndex, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countries))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, transportNationalityRoute)
          .withFormUrlEncodedBody(("value", "GB"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countries))

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, transportNationalityRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TransportNationalityView]

      contentAsString(result) mustEqual
        view(boundForm, countries.countries, mrn, eventIndex, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, transportNationalityRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, transportNationalityRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }

}
