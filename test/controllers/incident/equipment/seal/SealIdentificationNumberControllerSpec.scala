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

package controllers.incident.equipment.seal

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.incident.SealIdentificationFormProvider
import models.NormalMode
import navigation.SealNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.equipment.seal.SealIdentificationNumberPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.equipment.seal.SealIdentificationNumberView

import scala.concurrent.Future

class SealIdentificationNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val prefix = "incident.equipment.seal.sealIdentificationNumber"

  private val formProvider                      = new SealIdentificationFormProvider()
  private def form(otherIds: Seq[String] = Nil) = formProvider(prefix, otherIds)

  private val mode                               = NormalMode
  private lazy val sealIdentificationNumberRoute = routes.SealIdentificationNumberController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex, sealIndex).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[SealNavigatorProvider]).toInstance(fakeSealNavigatorProvider))

  "SealIdentificationNumber Controller" - {

    "must return OK and the correct view for a GET" - {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, sealIdentificationNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[SealIdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(), mrn, mode, incidentIndex, equipmentIndex, sealIndex, prefix)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {

      val userAnswers = emptyUserAnswers
        .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), "test")

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, sealIdentificationNumberRoute)

      val result = route(app, request).value

      val filledForm = form().bind(Map("value" -> "test"))

      val view = injector.instanceOf[SealIdentificationNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode, incidentIndex, equipmentIndex, sealIndex, prefix)(request, messages).toString

    }

    "must redirect to the next page when valid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.set(any())(any())) `thenReturn` Future.successful(true)

      val request = FakeRequest(POST, sealIdentificationNumberRoute)
        .withFormUrlEncodedBody(("value", "test"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" - {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, sealIdentificationNumberRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form().bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[SealIdentificationNumberView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode, incidentIndex, equipmentIndex, sealIndex, prefix)(request, messages).toString
    }
  }

  "must redirect to Session Expired for a GET if no existing data is found" in {

    setNoExistingUserAnswers()

    val request = FakeRequest(GET, sealIdentificationNumberRoute)

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
  }

  "must redirect to Session Expired for a POST if no existing data is found" in {

    setNoExistingUserAnswers()

    val request = FakeRequest(POST, sealIdentificationNumberRoute)
      .withFormUrlEncodedBody(("value", "test string"))

    val result = route(app, request).value

    status(result) mustEqual SEE_OTHER

    redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
  }
}
