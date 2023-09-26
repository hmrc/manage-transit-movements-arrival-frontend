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
import generators.Generators
import models.NormalMode
import models.identification.ProcedureType._
import models.reference.TypeOfLocation
import navigation.ArrivalNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.TypeOfLocationPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ReferenceDataDynamicRadioService
import views.html.locationOfGoods.TypeOfLocationView

import scala.concurrent.Future

class TypeOfLocationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val tois = arbitrary[Seq[TypeOfLocation]].sample.value
  private val toi  = tois.head

  private val formProvider             = new EnumerableFormProvider()
  private val form                     = formProvider[TypeOfLocation]("locationOfGoods.typeOfLocation", tois)
  private val mode                     = NormalMode
  private lazy val typeOfLocationRoute = routes.TypeOfLocationController.onPageLoad(mrn, mode).url

  private lazy val mockService: ReferenceDataDynamicRadioService = mock[ReferenceDataDynamicRadioService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[ArrivalNavigatorProvider]).toInstance(fakeArrivalNavigatorProvider))
      .overrides(bind(classOf[ReferenceDataDynamicRadioService]).toInstance(mockService))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockService)
    when(mockService.getTypesOfLocation()(any())).thenReturn(Future.successful(tois))
  }

  "TypeOfLocation Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Normal)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, typeOfLocationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TypeOfLocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, tois, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Simplified)
        .setValue(TypeOfLocationPage, toi)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, typeOfLocationRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> toi.code))

      val view = injector.instanceOf[TypeOfLocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, tois, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Normal)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, typeOfLocationRoute)
        .withFormUrlEncodedBody(("value", toi.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(IsSimplifiedProcedurePage, Simplified)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, typeOfLocationRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[TypeOfLocationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, tois, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, typeOfLocationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, typeOfLocationRoute)
        .withFormUrlEncodedBody(("value", toi.code))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
