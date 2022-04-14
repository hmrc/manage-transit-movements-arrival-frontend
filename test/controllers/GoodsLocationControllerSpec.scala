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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.GoodsLocationFormProvider
import models.{GoodsLocation, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.GoodsLocationPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.GoodsLocationView

import scala.concurrent.Future

class GoodsLocationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val mode                    = NormalMode
  private lazy val goodsLocationRoute = routes.GoodsLocationController.onPageLoad(mrn, mode).url

  private val formProvider = new GoodsLocationFormProvider()
  private val form         = formProvider()

  private val validAnswer = GoodsLocation.values.head

  "GoodsLocation Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, goodsLocationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[GoodsLocationView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, GoodsLocation.radioItems, mrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(GoodsLocationPage, validAnswer)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, goodsLocationRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[GoodsLocationView]

      status(result) mustEqual OK

      val filledForm = form.bind(Map("value" -> validAnswer.toString))

      contentAsString(result) mustEqual
        view(filledForm, GoodsLocation.radioItems, mrn, mode)(request, messages).toString
    }

    "must redirect to the correct page for Border Force Office when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.setValue(GoodsLocationPage, GoodsLocation.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, goodsLocationRoute)
        .withFormUrlEncodedBody(("value", GoodsLocation.BorderForceOffice.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual s"/manage-transit-movements/arrival/${emptyUserAnswers.movementReferenceNumber}/customs-approved-location"
    }

    "must redirect to the correct page for Authorised Consignees Location when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.setValue(GoodsLocationPage, GoodsLocation.values.head)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, goodsLocationRoute)
        .withFormUrlEncodedBody(("value", GoodsLocation.AuthorisedConsigneesLocation.toString))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual s"/manage-transit-movements/arrival/${emptyUserAnswers.movementReferenceNumber}/authorised-location-code"
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val invalidAnswer = "invalid value"

      val request = FakeRequest(POST, goodsLocationRoute)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form.bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[GoodsLocationView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, GoodsLocation.radioItems, mrn, mode)(request, messages).toString
    }
  }
}
