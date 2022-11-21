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

package controllers.incident.equipment.itemNumber

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalacheck.Gen
import pages.incident.equipment.itemNumber.ItemNumberPage
import pages.sections.incident.ItemSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.equipment.itemNumber.ConfirmRemoveItemNumberView

import scala.concurrent.Future

class ConfirmRemoveItemNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new YesNoFormProvider()
  private val itemNumber   = Gen.alphaNumStr.sample.value
  private val form         = formProvider("incident.equipment.itemNumber.remove", itemNumber)
  private val mode         = NormalMode

  private lazy val confirmRemoveItemNumberRoute =
    routes.ConfirmRemoveItemNumberController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex).url

  "ConfirmRemoveItemNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), itemNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, confirmRemoveItemNumberRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveItemNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex, itemNumber)(request, messages).toString
    }

    "must return error page when user tries to remove a item number that does not exist" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveItemNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to the next page when valid data is submitted and call to remove item" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), itemNumber)

      setExistingUserAnswers(userAnswers)

      val request =
        FakeRequest(POST, confirmRemoveItemNumberRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.AddAnotherItemNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())
      userAnswersCaptor.getValue.get(ItemSection(incidentIndex, equipmentIndex, itemNumberIndex)) mustNot be(defined)
    }

    "must redirect to the next page when valid data is submitted and call to remove a seal is false" in {

      val userAnswers = emptyUserAnswers
        .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), itemNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, confirmRemoveItemNumberRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.AddAnotherItemNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url

      verify(mockSessionRepository, never()).set(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(ItemNumberPage(incidentIndex, equipmentIndex, itemNumberIndex), itemNumber)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, confirmRemoveItemNumberRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveItemNumberView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, incidentIndex, equipmentIndex, itemNumberIndex, itemNumber)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveItemNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveItemNumberRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}