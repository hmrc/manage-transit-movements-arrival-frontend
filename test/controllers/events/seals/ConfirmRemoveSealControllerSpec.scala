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

package controllers.events.seals

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.events.seals.ConfirmRemoveSealFormProvider
import models.{Index, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.events.seals.SealIdentityPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConcurrentRemoveErrorView
import views.html.events.seals.ConfirmRemoveSealView

import scala.concurrent.Future

class ConfirmRemoveSealControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                 = new ConfirmRemoveSealFormProvider()
  private val form: Form[Boolean]          = formProvider(sealDomain)
  private val mode                         = NormalMode
  private lazy val removeSealRoute: String = routes.ConfirmRemoveSealController.onPageLoad(mrn, eventIndex, sealIndex, mode).url
  private val userAnswersWithSeal          = emptyUserAnswers.setValue(SealIdentityPage(eventIndex, sealIndex), sealDomain)

  "ConfirmRemoveSealController" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(userAnswersWithSeal)

      val request = FakeRequest(GET, removeSealRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveSealView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, eventIndex, sealIndex, mode, "sealNumber")(request, messages).toString
    }

    "must return error page when user tries to remove a seal that does not exists" in {
      val updatedAnswer = userAnswersWithSeal.removeValue(SealIdentityPage(eventIndex, sealIndex))
      setExistingUserAnswers(updatedAnswer)

      val request = FakeRequest(GET, removeSealRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConcurrentRemoveErrorView]

      status(result) mustEqual NOT_FOUND

      contentAsString(result) mustEqual
        view(mrn, "noSeal", onwardRoute.url, "concurrent.seal")(request, messages).toString
    }

    "must return error page when there are multiple seals and user tries to remove the last seal that is already removed" in {

      val updatedAnswer = userAnswersWithSeal
        .setValue(SealIdentityPage(eventIndex, Index(1)), sealDomain)
        .setValue(SealIdentityPage(eventIndex, Index(2)), sealDomain)
        .removeValue(SealIdentityPage(eventIndex, Index(2)))

      val sealRoute: String = routes.ConfirmRemoveSealController.onPageLoad(mrn, eventIndex, Index(2), NormalMode).url

      setExistingUserAnswers(updatedAnswer)

      val request = FakeRequest(GET, sealRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConcurrentRemoveErrorView]

      status(result) mustEqual NOT_FOUND

      contentAsString(result) mustEqual
        view(mrn, "multipleSeal", onwardRoute.url, "concurrent.seal")(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and seal is removed" in {

      setExistingUserAnswers(userAnswersWithSeal)

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val request = FakeRequest(POST, removeSealRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      val newUserAnswers = UserAnswers(
        movementReferenceNumber = userAnswersWithSeal.movementReferenceNumber,
        eoriNumber = userAnswersWithSeal.eoriNumber,
        userAnswersWithSeal.removeValue(SealIdentityPage(eventIndex, sealIndex)).data,
        userAnswersWithSeal.lastUpdated,
        id = userAnswersWithSeal.id
      )

      verify(mockSessionRepository, times(1)).set(newUserAnswers)
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(userAnswersWithSeal)

      val request   = FakeRequest(POST, removeSealRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveSealView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, eventIndex, sealIndex, mode, "sealNumber")(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, removeSealRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, removeSealRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
