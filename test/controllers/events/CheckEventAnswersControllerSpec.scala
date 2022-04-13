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
import generators.{MessagesModelGenerators, ViewModelGenerators}
import models.reference.Country
import models.{CountryList, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import services.CountriesService
import viewModels.CheckEventAnswersViewModel
import viewModels.sections.Section
import views.html.events.CheckEventAnswersView

import scala.concurrent.Future

class CheckEventAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MessagesModelGenerators with ViewModelGenerators {

  private lazy val mockCountriesService = mock[CountriesService]
  private lazy val mockViewModel        = mock[CheckEventAnswersViewModel]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CountriesService].toInstance(mockCountriesService))
      .overrides(bind[CheckEventAnswersViewModel].toInstance(mockViewModel))

  "Check Event Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      val sampleCountryList = arbitrary[Seq[Country]].sample.value
      val sampleSections    = arbitrary[Seq[Section]].sample.value

      when(mockCountriesService.getTransitCountries()(any()))
        .thenReturn(Future.successful(CountryList(sampleCountryList)))

      when(mockViewModel.apply(any(), any(), any(), any())(any()))
        .thenReturn(sampleSections)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, routes.CheckEventAnswersController.onPageLoad(mrn, eventIndex).url)

      val result = route(app, request).value

      val view = injector.instanceOf[CheckEventAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(mrn, eventIndex, sampleSections)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.CheckEventAnswersController.onPageLoad(mrn, eventIndex).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Add event page" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockRenderer.render(any(), any())(any())).thenReturn(Future.successful(Html("")))

      val request = FakeRequest(POST, controllers.events.routes.CheckEventAnswersController.onSubmit(mrn, eventIndex).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.events.routes.AddEventController.onPageLoad(mrn, NormalMode).url
    }
  }
}
