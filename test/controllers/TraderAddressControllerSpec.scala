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
import forms.TraderAddressFormProvider
import models.NormalMode
import pages.{TraderAddressPage, TraderNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.TraderAddressView

class TraderAddressControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new TraderAddressFormProvider()
  private val form         = formProvider(traderName)

  private val mode = NormalMode

  private lazy val traderAddressRoute = routes.TraderAddressController.onPageLoad(mrn, NormalMode).url

  "Address Controller" - {

    "must return OK and the correct view for a GET" in {

      val answers = emptyUserAnswers.setValue(TraderNamePage, traderName)

      setExistingUserAnswers(answers)

      val request = FakeRequest(GET, traderAddressRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TraderAddressView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode, traderName)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .setValue(TraderNamePage, traderName)
        .setValue(TraderAddressPage, traderAddress)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, traderAddressRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[TraderAddressView]

      status(result) mustEqual OK

      val filledForm = form.bind(
        Map(
          "buildingAndStreet" -> traderAddress.buildingAndStreet,
          "city"              -> traderAddress.city,
          "postcode"          -> traderAddress.postcode
        )
      )

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode, traderName)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(TraderNamePage, traderName)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, traderAddressRoute)
        .withFormUrlEncodedBody(
          ("buildingAndStreet", traderAddress.buildingAndStreet),
          ("city", traderAddress.city),
          ("postcode", traderAddress.postcode)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(TraderNamePage, traderName)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, traderAddressRoute).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[TraderAddressView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, traderName)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, traderAddressRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, traderAddressRoute)
        .withFormUrlEncodedBody(
          ("buildingAndStreet", traderAddress.buildingAndStreet),
          ("city", traderAddress.city),
          ("postcode", traderAddress.postcode)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
