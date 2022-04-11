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

package controllers.events.transhipments

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.events.transhipments.ConfirmRemoveContainerFormProvider
import models.{Index, NormalMode, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.events.transhipments.ContainerNumberPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ConcurrentRemoveErrorView
import views.html.events.transhipments.ConfirmRemoveContainerView

import scala.concurrent.Future

class ConfirmRemoveContainerControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider = new ConfirmRemoveContainerFormProvider()
  private val form         = formProvider(domainContainer)

  private val mode = NormalMode

  private lazy val confirmRemoveContainerRoute = routes.ConfirmRemoveContainerController.onPageLoad(mrn, eventIndex, containerIndex, mode).url

  private val presetUserAnswers = emptyUserAnswers.setValue(ContainerNumberPage(eventIndex, containerIndex), domainContainer)

  "ConfirmRemoveContainer Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(presetUserAnswers)

      val request = FakeRequest(GET, confirmRemoveContainerRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveContainerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, eventIndex, containerIndex, mode, domainContainer.containerNumber)(request, messages).toString
    }

    "must return error page when user tries to remove a container that does not exists" in {

      val updatedAnswer = presetUserAnswers.remove(ContainerNumberPage(eventIndex, containerIndex)).success.value
      setExistingUserAnswers(updatedAnswer)

      val request = FakeRequest(GET, confirmRemoveContainerRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ConcurrentRemoveErrorView]

      status(result) mustEqual NOT_FOUND

      contentAsString(result) mustEqual
        view(mrn, "noContainer", onwardRoute.url, "container")(request, messages).toString
    }

    "must return error page when there are multiple containers and user tries to remove the last container that is already removed" in {

      val updatedAnswer = presetUserAnswers
        .setValue(ContainerNumberPage(eventIndex, Index(1)), domainContainer)
        .setValue(ContainerNumberPage(eventIndex, Index(2)), domainContainer)
        .removeValue(ContainerNumberPage(eventIndex, Index(2)))

      val removeContainerRoute = routes.ConfirmRemoveContainerController.onPageLoad(mrn, eventIndex, Index(2), NormalMode).url

      setExistingUserAnswers(updatedAnswer)

      val request = FakeRequest(GET, removeContainerRoute)

      val result = route(app, request).value

      status(result) mustEqual NOT_FOUND

      val view = injector.instanceOf[ConcurrentRemoveErrorView]

      contentAsString(result) mustEqual
        view(mrn, "multipleContainer", onwardRoute.url, "container")(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted and call to remove data when true" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(presetUserAnswers)

      val request =
        FakeRequest(POST, confirmRemoveContainerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      val updateAnswers = UserAnswers(
        id = presetUserAnswers.id,
        eoriNumber = presetUserAnswers.eoriNumber,
        data = presetUserAnswers.remove(ContainerNumberPage(eventIndex, containerIndex)).success.value.data,
        lastUpdated = presetUserAnswers.lastUpdated,
        movementReferenceNumber = presetUserAnswers.movementReferenceNumber
      )

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      verify(mockSessionRepository, times(1)).set(updateAnswers)
    }

    "must redirect to the next page when valid data is submitted and not call to remove data when false" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(presetUserAnswers)

      val request =
        FakeRequest(POST, confirmRemoveContainerRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(app, request).value

      val updateAnswers = UserAnswers(
        id = presetUserAnswers.id,
        eoriNumber = eoriNumber,
        data = presetUserAnswers.remove(ContainerNumberPage(eventIndex, containerIndex)).success.value.data,
        lastUpdated = presetUserAnswers.lastUpdated,
        movementReferenceNumber = presetUserAnswers.movementReferenceNumber
      )

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      verify(mockSessionRepository, times(0)).set(updateAnswers)
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(presetUserAnswers)

      val request   = FakeRequest(POST, confirmRemoveContainerRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveContainerView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, eventIndex, containerIndex, mode, domainContainer.containerNumber)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveContainerRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, confirmRemoveContainerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
