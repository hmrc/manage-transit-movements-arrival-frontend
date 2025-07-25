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
import forms.UnLocodeFormProvider
import generators.Generators
import models.NormalMode
import navigation.ArrivalNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.locationOfGoods.UnlocodePage
import play.api.data.FormError
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import services.UnLocodeService
import views.html.locationOfGoods.UnlocodeView

import scala.concurrent.Future

class UnlocodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val prefix = "locationOfGoods.unlocode"

  private val unLocode1 = arbitraryUnLocode.arbitrary.sample.get.unLocodeExtendedCode

  private val formProvider       = new UnLocodeFormProvider()
  private val form               = formProvider(prefix)
  private val mode               = NormalMode
  private lazy val unlocodeRoute = routes.UnlocodeController.onPageLoad(mrn, mode).url

  private val mockUnLocodesService: UnLocodeService = mock[UnLocodeService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[UnLocodeService]).toInstance(mockUnLocodesService))
      .overrides(bind(classOf[ArrivalNavigatorProvider]).toInstance(fakeArrivalNavigatorProvider))

  "Unlocode Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, unlocodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[UnlocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(UnlocodePage, unLocode1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, unlocodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> unLocode1))

      val view = injector.instanceOf[UnlocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockUnLocodesService.doesUnLocodeExist(any())(any())) `thenReturn` Future.successful(true)

      when(mockSessionRepository.set(any())(any())) `thenReturn` Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, unlocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when value does not exist in the reference data collection" in {

      when(mockUnLocodesService.doesUnLocodeExist(any())(any())) `thenReturn` Future.successful(false)

      when(mockSessionRepository.set(any())(any())) `thenReturn` Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val unknownAnswer = "ABCDE"

      val request = FakeRequest(POST, unlocodeRoute)
        .withFormUrlEncodedBody(("value", unknownAnswer))

      val filledForm = form.bind(Map("value" -> unknownAnswer)).withError(FormError("value", s"$prefix.error.not.exists"))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[UnlocodeView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, unlocodeRoute).withFormUrlEncodedBody(("value", invalidAnswer))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[UnlocodeView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, unlocodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, unlocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }
  }
}
