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
import forms.events.transhipments.AddContainerFormProvider
import models.domain.ContainerDomain
import models.{Index, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.events.transhipments.{AddContainerPage, ContainerNumberPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import views.html.events.transhipments.AddContainerView

import scala.concurrent.Future

class AddContainerControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                       = new AddContainerFormProvider()
  private def form(allowMoreContainers: Boolean) = formProvider(allowMoreContainers)

  private val mode = NormalMode

  private lazy val addContainerRoute = routes.AddContainerController.onPageLoad(mrn, eventIndex, mode).url

  private val range = 0 to 2

  private val maxedUserAnswers = range.foldLeft(emptyUserAnswers) {
    (acc, i) =>
      acc.setValue(ContainerNumberPage(eventIndex, Index(i)), ContainerDomain(s"$i"))
  }

  private lazy val maxedListItems = range.map {
    i =>
      ListItem(
        name = s"$i",
        changeUrl = routes.ContainerNumberController.onPageLoad(mrn, eventIndex, Index(i), mode).url,
        removeUrl = routes.ConfirmRemoveContainerController.onPageLoad(mrn, eventIndex, Index(i), mode).url
      )
  }

  "AddContainer Controller" - {

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreContainers = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addContainerRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddContainerView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreContainers), mrn, eventIndex, mode, (_, _) => Nil, _ => allowMoreContainers)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreContainers = false

        setExistingUserAnswers(maxedUserAnswers)

        val request = FakeRequest(GET, addContainerRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddContainerView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreContainers), mrn, eventIndex, mode, (_, _) => maxedListItems, _ => allowMoreContainers)(request, messages).toString
      }
    }

    "must populate the view correctly on a GET when the question has previously been answered" - {
      "when max limit not reached" in {

        val allowMoreContainers = true

        val userAnswers = emptyUserAnswers.setValue(AddContainerPage(eventIndex), true)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addContainerRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddContainerView]

        status(result) mustEqual OK

        val filledForm = form(allowMoreContainers).bind(Map("value" -> "true"))

        contentAsString(result) mustEqual
          view(filledForm, mrn, eventIndex, mode, (_, _) => Nil, _ => allowMoreContainers)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreContainers = false

        val userAnswers = maxedUserAnswers.setValue(AddContainerPage(eventIndex), true)
        setExistingUserAnswers(userAnswers)

        val request = FakeRequest(GET, addContainerRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddContainerView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreContainers), mrn, eventIndex, mode, (_, _) => maxedListItems, _ => allowMoreContainers)(request, messages).toString
      }
    }

    "must redirect to the next page" - {
      "when valid data is submitted and max limit not reached" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addContainerRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }

      "when max limit reached" in {

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        setExistingUserAnswers(maxedUserAnswers)

        val request = FakeRequest(POST, addContainerRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {

        val allowMoreContainers = true

        setExistingUserAnswers(emptyUserAnswers)

        val request   = FakeRequest(POST, addContainerRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form(allowMoreContainers).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddContainerView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, eventIndex, mode, (_, _) => Nil, _ => allowMoreContainers)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addContainerRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addContainerRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
