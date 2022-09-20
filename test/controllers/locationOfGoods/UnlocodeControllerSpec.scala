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

package controllers.locationOfGoods

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.locationOfGoods.UnLocodeFormProvider
import generators.Generators
import models.{NormalMode, UnLocodeList}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.locationOfGoods.UnlocodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UnLocodeService
import views.html.locationOfGoods.UnlocodeView

import scala.concurrent.Future

class UnlocodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val unLocode1    = arbitraryUnLocode.arbitrary.sample.get
  private val unLocode2    = arbitraryUnLocode.arbitrary.sample.get
  private val unLocodeList = UnLocodeList(Seq(unLocode1, unLocode2))

  private val formProvider       = new UnLocodeFormProvider()
  private val form               = formProvider("locationOfGoods.unlocode", unLocodeList)
  private val mode               = NormalMode
  private lazy val unlocodeRoute = routes.UnlocodeController.onPageLoad(mrn, mode).url

  private val mockUnLocodesService: UnLocodeService = mock[UnLocodeService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[UnLocodeService]).toInstance(mockUnLocodesService))

  "Unlocode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, unlocodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[UnlocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, unLocodeList.unLocodes, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      val userAnswers = emptyUserAnswers.setValue(UnlocodePage, unLocode1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, unlocodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> unLocode1.unLocodeExtendedCode))

      val view = injector.instanceOf[UnlocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, unLocodeList.unLocodes, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, unlocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1.unLocodeExtendedCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockUnLocodesService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = ""

      val request    = FakeRequest(POST, unlocodeRoute).withFormUrlEncodedBody(("value", ""))
      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[UnlocodeView]

      contentAsString(result) mustEqual
        view(filledForm, mrn, unLocodeList.unLocodes, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, unlocodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, unlocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1.unLocodeExtendedCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
