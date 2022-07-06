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

package controllers.identification

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.IdentificationDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import forms.DateFormProvider
import views.html.identification.ArrivalDateView
import pages.identification.ArrivalDatePage
import java.time.{Clock, LocalDate, ZoneOffset}

import scala.concurrent.Future

class ArrivalDateControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val minDate = LocalDate.of(2020: Int, 12: Int, 31: Int) //"31 December 2020"
  private val zone    = ZoneOffset.UTC
  private val clock   = Clock.systemDefaultZone.withZone(zone)

  private val formProvider          = new DateFormProvider(clock)
  private val form                  = formProvider("identification.arrivalDate", minDate)
  private val mode                  = NormalMode
  private lazy val arrivalDateRoute = routes.ArrivalDateController.onPageLoad(mrn, mode).url
  private val date                  = LocalDate.now

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[IdentificationDetails]).toInstance(fakeNavigator))

  "ArrivalDate Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, arrivalDateRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ArrivalDateView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode)(request, messages).toString

    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(mrn, eoriNumber).set(ArrivalDatePage, date).success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, arrivalDateRoute)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "value.day"   -> date.getDayOfMonth.toString,
          "value.month" -> date.getMonthValue.toString,
          "value.year"  -> date.getYear.toString
        )
      )

      val view = injector.instanceOf[ArrivalDateView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request =
        FakeRequest(POST, arrivalDateRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> date.getDayOfMonth.toString,
            "value.month" -> date.getMonthValue.toString,
            "value.year"  -> date.getYear.toString
          )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, arrivalDateRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ArrivalDateView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString

    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, arrivalDateRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, arrivalDateRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> date.getDayOfMonth.toString,
            "value.month" -> date.getMonthValue.toString,
            "value.year"  -> date.getYear.toString
          )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

    }
  }
}
