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
import forms.CustomsOfficeFormProvider
import models.reference.CustomsOffice
import models.{CustomsOfficeList, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import pages.{ConsigneeNamePage, CustomsOfficePage, CustomsSubPlacePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.CustomsOfficesService
import views.html.CustomsOfficeView

import scala.concurrent.Future

class CustomsOfficeControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val mode              = NormalMode
  private val formProvider      = new CustomsOfficeFormProvider()
  private val customsOfficeList = CustomsOfficeList(Seq(CustomsOffice("id", Some("name"), None), CustomsOffice("officeId", Some("someName"), None)))

  private val locationName: String   = "sub place"
  override val consigneeName: String = "consignee place"

  private def form(subPlace: String): Form[CustomsOffice] = formProvider(subPlace, customsOfficeList)

  private lazy val customsOfficeRoute: String                       = routes.CustomsOfficeController.onPageLoad(mrn, mode).url
  private lazy val mockCustomsOfficesService: CustomsOfficesService = mock[CustomsOfficesService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockCustomsOfficesService)
    when(mockCustomsOfficesService.getCustomsOfficesOfArrival(any())).thenReturn(Future.successful(customsOfficeList))
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[CustomsOfficesService].toInstance(mockCustomsOfficesService))

  "CustomsOffice Controller" - {

    "must return OK and the correct view for a GET" - {
      "when CustomsSubPlacePage is populated" in {
        setExistingUserAnswers(emptyUserAnswers.setValue(CustomsSubPlacePage, locationName))

        val request = FakeRequest(GET, customsOfficeRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(locationName), customsOfficeList.customsOffices, mrn, mode, locationName)(request, messages).toString
      }

      "when CustomsSubPlacePage is not populated and ConsigneeNamePage is populated" in {
        setExistingUserAnswers(emptyUserAnswers.setValue(ConsigneeNamePage, consigneeName))

        val request = FakeRequest(GET, customsOfficeRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[CustomsOfficeView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(consigneeName), customsOfficeList.customsOffices, mrn, mode, consigneeName)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val officeId   = "officeId"
      val officeName = "someName"

      val filledForm = form(locationName).bind(Map("value" -> officeId))

      val userAnswers = emptyUserAnswers
        .setValue(CustomsSubPlacePage, locationName)
        .setValue(CustomsOfficePage, CustomsOffice(officeId, Some(officeName), None))

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, customsOfficeRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, customsOfficeList.customsOffices, mrn, mode, locationName)(request, messages).toString
    }

    "must redirect to session expired page when user hasn't answered the customs sub place question" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, customsOfficeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue(CustomsSubPlacePage, locationName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, customsOfficeRoute)
        .withFormUrlEncodedBody(("value", "id"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
      verify(mockCustomsOfficesService, times(1)).getCustomsOfficesOfArrival(any())
    }

    "must return Bad Request and error when user entered data does not exist in reference data customs office list" in {

      setExistingUserAnswers(emptyUserAnswers.setValue(CustomsSubPlacePage, locationName))

      val invalidAnswer = "invalid value"

      val request = FakeRequest(POST, customsOfficeRoute)
        .withFormUrlEncodedBody(("value", invalidAnswer))

      val filledForm = form(locationName).bind(Map("value" -> invalidAnswer))

      val result = route(app, request).value

      val view = injector.instanceOf[CustomsOfficeView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(filledForm, customsOfficeList.customsOffices, mrn, mode, locationName)(request, messages).toString
    }

    "must redirect to session expired page when invalid data is submitted and user hasn't answered the customs sub-place page question" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, customsOfficeRoute)
        .withFormUrlEncodedBody(("value", ""))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, customsOfficeRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, customsOfficeRoute)
        .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
}
