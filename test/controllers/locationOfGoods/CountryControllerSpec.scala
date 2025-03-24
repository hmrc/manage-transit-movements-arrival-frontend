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

package controllers.locationOfGoods

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.SelectableFormProvider.CountryFormProvider
import generators.Generators
import models.{NormalMode, SelectableList}
import navigation.ArrivalNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.locationOfGoods.CountryPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CountriesService
import views.html.locationOfGoods.CountryView

import scala.concurrent.Future

class CountryControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val country1    = arbitraryCountry.arbitrary.sample.value
  private val country2    = arbitraryCountry.arbitrary.sample.value
  private val countryList = SelectableList(Seq(country1, country2))

  private val formProvider = new CountryFormProvider()
  private val form         = formProvider("locationOfGoods.country", countryList)
  private val field        = formProvider.field
  private val mode         = NormalMode

  private val mockCountriesService: CountriesService = mock[CountriesService]
  private lazy val countryRoute                      = routes.CountryController.onPageLoad(mrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ArrivalNavigatorProvider]).toInstance(fakeArrivalNavigatorProvider))
      .overrides(bind(classOf[CountriesService]).toInstance(mockCountriesService))

  "Country Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, countryRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, countryList.values, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      val userAnswers = emptyUserAnswers.setValue(CountryPage, country1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, countryRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map(field -> country1.code.code))

      val view = injector.instanceOf[CountryView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, countryList.values, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      when(mockSessionRepository.set(any())(any())) `thenReturn` Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, countryRoute)
        .withFormUrlEncodedBody((field, country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockCountriesService.getCountries()(any())).thenReturn(Future.successful(countryList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, countryRoute).withFormUrlEncodedBody((field, "invalid value"))
      val boundForm = form.bind(Map(field -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[CountryView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, countryList.values, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, countryRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, countryRoute)
        .withFormUrlEncodedBody((field, country1.code.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }
  }
}
