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
import forms.EnumerableFormProvider
import models.identification.ProcedureType._
import models.{NormalMode, QualifierOfIdentification}
import navigation.ArrivalNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.QualifierOfIdentificationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.locationOfGoods.QualifierOfIdentificationView

import scala.concurrent.Future

class QualifierOfIdentificationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                        = new EnumerableFormProvider()
  private val form                                = formProvider[QualifierOfIdentification]("locationOfGoods.qualifierOfIdentification")
  private val mode                                = NormalMode
  private lazy val qualifierOfIdentificationRoute = routes.QualifierOfIdentificationController.onPageLoad(mrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ArrivalNavigatorProvider]).toInstance(fakeArrivalNavigatorProvider))

  "QualifierOfIdentification Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Normal)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, qualifierOfIdentificationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[QualifierOfIdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, QualifierOfIdentification.values(userAnswers), mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Simplified)
        .setValue(QualifierOfIdentificationPage, QualifierOfIdentification.values.head)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, qualifierOfIdentificationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> QualifierOfIdentification.values.head.toString))

      val view = injector.instanceOf[QualifierOfIdentificationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, QualifierOfIdentification.values(userAnswers), mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Normal)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, qualifierOfIdentificationRoute)
        .withFormUrlEncodedBody(("value", QualifierOfIdentification.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Simplified)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, qualifierOfIdentificationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[QualifierOfIdentificationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, QualifierOfIdentification.values(userAnswers), mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, qualifierOfIdentificationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, qualifierOfIdentificationRoute)
        .withFormUrlEncodedBody(("value", QualifierOfIdentification.values.head.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
