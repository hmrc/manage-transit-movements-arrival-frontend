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

package controllers.incident.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import pages.sections.incident.EquipmentSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.equipment.ConfirmRemoveEquipmentView

import scala.concurrent.Future

class ConfirmRemoveEquipmentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators with ArrivalUserAnswersGenerator {

  private val prefix       = "incident.equipment.remove"
  private val formProvider = new YesNoFormProvider()
  private val form         = formProvider(prefix, equipmentIndex.display)

  private val mode                    = NormalMode
  private lazy val confirmRemoveRoute = routes.ConfirmRemoveEquipmentController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex).url

  "ConfirmRemoveSeal Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

      val view = injector.instanceOf[ConfirmRemoveEquipmentView]

      contentAsString(result) mustEqual
        view(form, mrn, mode, incidentIndex, equipmentIndex)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and call to remove a transport equipment" in {
      forAll(arbitraryEquipmentAnswers(emptyUserAnswers, incidentIndex, equipmentIndex)) {
        userAnswers =>
          reset(mockSessionRepository)
          when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

          setExistingUserAnswers(userAnswers)

          val request = FakeRequest(POST, confirmRemoveRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.AddAnotherEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())
          userAnswersCaptor.getValue.get(EquipmentSection(incidentIndex, equipmentIndex)) mustNot be(defined)
      }
    }

    "must redirect to the next page when valid data is submitted and call to remove a transport equipment is false" in {

      val userAnswers = emptyUserAnswers

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.AddAnotherEquipmentController.onPageLoad(userAnswers.mrn, mode, incidentIndex).url

      verify(mockSessionRepository, never()).set(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, confirmRemoveRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveEquipmentView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, incidentIndex, equipmentIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
