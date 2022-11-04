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

package controllers.incident.location

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.UnLocodeFormProvider
import generators.Generators
import models.{NormalMode, UnLocodeList}
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.incident.location.UnLocodePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.UnLocodeService
import views.html.incident.location.UnLocodeView

import scala.concurrent.Future

class UnLocodeControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val unLocode1    = arbitraryUnLocode.arbitrary.sample.get
  private val unLocode2    = arbitraryUnLocode.arbitrary.sample.get
  private val unLocodeList = UnLocodeList(Seq(unLocode1, unLocode2))

  private val formProvider = new UnLocodeFormProvider()
  private val form         = formProvider("incident.location.unLocode", unLocodeList)
  private val mode         = NormalMode

  private val mockUnLocodeService: UnLocodeService = mock[UnLocodeService]
  private lazy val unLocodeRoute                   = routes.UnLocodeController.onPageLoad(mrn, mode, index).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))
      .overrides(bind(classOf[UnLocodeService]).toInstance(mockUnLocodeService))

  "UnLocode Controller" - {

    "must return OK and the correct view for a GET" in {

      when(mockUnLocodeService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, unLocodeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[UnLocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, unLocodeList.unLocodes, mode, index)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      when(mockUnLocodeService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      val userAnswers = emptyUserAnswers.setValue(UnLocodePage(index), unLocode1)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, unLocodeRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> unLocode1.unLocodeExtendedCode))

      val view = injector.instanceOf[UnLocodeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, unLocodeList.unLocodes, mode, index)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockUnLocodeService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, unLocodeRoute)
          .withFormUrlEncodedBody(("value", unLocode1.unLocodeExtendedCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      when(mockUnLocodeService.getUnLocodes()(any())).thenReturn(Future.successful(unLocodeList))
      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, unLocodeRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[UnLocodeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, unLocodeList.unLocodes, mode, index)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, unLocodeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, unLocodeRoute)
        .withFormUrlEncodedBody(("value", unLocode1.unLocodeExtendedCode))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}