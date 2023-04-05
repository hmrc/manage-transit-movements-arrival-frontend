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

package controllers.incident.equipment.seal

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import org.scalacheck.Gen
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.sections.incident.SealSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.equipment.seal.ConfirmRemoveSealView

import scala.concurrent.Future

class ConfirmRemoveSealControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val prefix               = "incident.equipment.seal.remove"
  private val identificationNumber = Gen.alphaNumStr.sample.value
  private val formProvider         = new YesNoFormProvider()
  private val form                 = formProvider(prefix, identificationNumber)

  private val mode                    = NormalMode
  private lazy val confirmRemoveRoute = routes.ConfirmRemoveSealController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex, sealIndex).url

  "ConfirmRemoveSeal Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), identificationNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, confirmRemoveRoute)
      val result  = route(app, request).value

      status(result) mustEqual OK

      val view = injector.instanceOf[ConfirmRemoveSealView]

      contentAsString(result) mustEqual
        view(form, mrn, mode, incidentIndex, equipmentIndex, sealIndex, identificationNumber)(request, messages).toString
    }

    "must return error page when user tries to remove a seal that does not exist" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to the next page when valid data is submitted and call to remove a seal" in {
      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers
        .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), identificationNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
      userAnswersCaptor.getValue.get(SealSection(incidentIndex, equipmentIndex, sealIndex)) mustNot be(defined)
    }

    "must redirect to the next page when valid data is submitted and call to remove a seal is false" in {

      val userAnswers = emptyUserAnswers
        .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), identificationNumber)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, confirmRemoveRoute)
        .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        routes.AddAnotherSealController.onPageLoad(userAnswers.mrn, mode, incidentIndex, equipmentIndex).url

      verify(mockSessionRepository, never()).set(any())(any())
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .setValue(SealIdentificationNumberPage(incidentIndex, equipmentIndex, sealIndex), identificationNumber)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, confirmRemoveRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))
      val result    = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveSealView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, incidentIndex, equipmentIndex, sealIndex, identificationNumber)(request, messages).toString
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
