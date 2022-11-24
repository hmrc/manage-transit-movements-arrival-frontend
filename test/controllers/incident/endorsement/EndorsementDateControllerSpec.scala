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

package controllers.incident.endorsement

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.DateFormProvider
import models.NormalMode
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.endorsement
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.endorsement.EndorsementDateView

import java.time.{Clock, LocalDate, ZoneOffset}
import scala.concurrent.Future

class EndorsementDateControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val minDate = frontendAppConfig.endorsementDateMin
  private val zone    = ZoneOffset.UTC
  private val clock   = Clock.systemDefaultZone.withZone(zone)

  private val formProvider              = new DateFormProvider(clock)
  private val form                      = formProvider("incident.endorsement.date", minDate)
  private val mode                      = NormalMode
  private lazy val endorsementDateRoute = routes.EndorsementDateController.onPageLoad(mrn, index, mode).url
  private val date                      = LocalDate.now

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))

  "EndorsementDate Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, endorsementDateRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[EndorsementDateView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, index, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(endorsement.EndorsementDatePage(index), date)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, endorsementDateRoute)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "value.day"   -> date.getDayOfMonth.toString,
          "value.month" -> date.getMonthValue.toString,
          "value.year"  -> date.getYear.toString
        )
      )

      val view = injector.instanceOf[EndorsementDateView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, index, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, endorsementDateRoute)
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

      val request    = FakeRequest(POST, endorsementDateRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[EndorsementDateView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, index, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, endorsementDateRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, endorsementDateRoute)
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
