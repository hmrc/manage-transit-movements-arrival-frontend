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

package controllers.incident.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import models.NormalMode
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import org.scalatestplus.mockito.MockitoSugar
import pages.incident.equipment.{AddSealsYesNoPage, ContainerIdentificationNumberPage}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.equipment.AddSealsYesNoView

import scala.concurrent.Future

class AddSealsYesNoControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar {

  private val formProvider            = new YesNoFormProvider()
  private val number: String          = Gen.alphaNumStr.sample.value
  private val form                    = formProvider("incident.equipment.addSealsYesNo", number)
  private val mode                    = NormalMode
  private lazy val addSealsYesNoRoute = routes.AddSealsYesNoController.onPageLoad(mrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))

  "AddSealsYesNo Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswer = emptyUserAnswers
        .setValue(ContainerIdentificationNumberPage(index), number)

      setExistingUserAnswers(userAnswer)

      val request = FakeRequest(GET, addSealsYesNoRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[AddSealsYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, number, mrn, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(ContainerIdentificationNumberPage(index), number)
        .setValue(AddSealsYesNoPage(index), true)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addSealsYesNoRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "true"))

      val view = injector.instanceOf[AddSealsYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, number, mrn, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswer = emptyUserAnswers
        .setValue(ContainerIdentificationNumberPage(index), number)

      setExistingUserAnswers(userAnswer)

      val request =
        FakeRequest(POST, addSealsYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswer = emptyUserAnswers
        .setValue(ContainerIdentificationNumberPage(index), number)

      setExistingUserAnswers(userAnswer)

      val request   = FakeRequest(POST, addSealsYesNoRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[AddSealsYesNoView]

      contentAsString(result) mustEqual
        view(boundForm, number, mrn, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addSealsYesNoRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addSealsYesNoRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
