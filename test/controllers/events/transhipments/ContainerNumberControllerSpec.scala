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
import forms.events.transhipments.ContainerNumberFormProvider
import generators.Generators
import models.domain.ContainerDomain
import models.{Index, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.events.transhipments.ContainerNumberPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.events.transhipments.ContainerNumberView

import scala.concurrent.Future

class ContainerNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider                                                                                      = new ContainerNumberFormProvider()
  private def form(index: Index = containerIndex, declaredContainers: Seq[ContainerDomain] = Nil): Form[String] = formProvider(index, declaredContainers)
  private val mode                                                                                              = NormalMode

  private def containerNumberRoute(index: Index = containerIndex): String = routes.ContainerNumberController.onPageLoad(mrn, eventIndex, index, mode).url

  "ContainerNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, containerNumberRoute())

      val result = route(app, request).value

      val view = injector.instanceOf[ContainerNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form(), mrn, eventIndex, containerIndex, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(ContainerNumberPage(eventIndex, containerIndex), ContainerDomain("answer"))
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, containerNumberRoute())

      val result = route(app, request).value

      val view = injector.instanceOf[ContainerNumberView]

      val filledForm = form().bind(Map("value" -> "answer"))

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, eventIndex, containerIndex, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, containerNumberRoute()).withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the next page when a value that is the same as the previous is submitted within the same index" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val container   = arbitrary[ContainerDomain].sample.value
      val userAnswers = emptyUserAnswers.setValue(ContainerNumberPage(eventIndex, containerIndex), container)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, containerNumberRoute()).withFormUrlEncodedBody(("value", container.containerNumber))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when an existing container number is submitted" in {
      val container   = arbitrary[ContainerDomain].sample.value
      val userAnswers = emptyUserAnswers.setValue(ContainerNumberPage(eventIndex, containerIndex), container)
      val nextIndex   = Index(1)

      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, containerNumberRoute(nextIndex)).withFormUrlEncodedBody(("value", container.containerNumber))
      val result    = route(app, request).value
      val boundForm = form(nextIndex, Seq(container)).bind(Map("value" -> container.containerNumber))

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ContainerNumberView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, eventIndex, nextIndex, mode)(request, messages).toString
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, containerNumberRoute()).withFormUrlEncodedBody(("value", ""))
      val boundForm = form().bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ContainerNumberView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, eventIndex, containerIndex, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, containerNumberRoute())

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, containerNumberRoute())
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
