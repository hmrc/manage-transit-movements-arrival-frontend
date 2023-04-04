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

package controllers.identification

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.identification.MovementReferenceNumberFormProvider
import models.{MovementReferenceNumber, NormalMode}
import navigation.ArrivalNavigatorProvider
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UserAnswersService
import views.html.identification.MovementReferenceNumberView

import scala.concurrent.Future

class MovementReferenceNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  val formProvider                        = new MovementReferenceNumberFormProvider()
  val form: Form[MovementReferenceNumber] = formProvider()

  private val mode = NormalMode

  private lazy val movementReferenceNumberRoute = routes.MovementReferenceNumberController.onPageLoad(mode).url

  private lazy val mockUserAnswersService = mock[UserAnswersService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ArrivalNavigatorProvider]).toInstance(fakeArrivalNavigatorProvider))
      .overrides(bind[UserAnswersService].toInstance(mockUserAnswersService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockUserAnswersService)
  }

  "MovementReferenceNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, movementReferenceNumberRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[MovementReferenceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and sessionRepository returns UserAnswers value as Some(_)" in {

      when(mockSessionRepository.get(any())(any())) thenReturn Future.successful(None) thenReturn Future.successful(Some(emptyUserAnswers))
      when(mockSessionRepository.put(any())(any())) thenReturn Future.successful(true)

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, movementReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", mrn.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
      verify(mockSessionRepository, times(2)).get(eqTo(mrn.toString))(any())
      verify(mockSessionRepository, times(1)).put(eqTo(mrn.toString))(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request = FakeRequest(POST, movementReferenceNumberRoute)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[MovementReferenceNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, mode)(request, messages).toString
    }
  }
}
