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
import forms.events.seals.AddSealFormProvider
import models.domain.SealDomain
import models.{Index, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, verify, when}
import pages.events.seals.SealIdentityPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import views.html.events.seals.AddSealView

import scala.concurrent.Future

class AddSealControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val formProvider                  = new AddSealFormProvider()
  private def form(allowMoreSeals: Boolean) = formProvider(allowMoreSeals)

  private val mode = NormalMode

  private lazy val addSealRoute = routes.AddSealController.onPageLoad(mrn, eventIndex, mode).url

  private val range = 0 to 2

  private val maxedUserAnswers = range.foldLeft(emptyUserAnswers) {
    (acc, i) =>
      acc.setValue(SealIdentityPage(eventIndex, Index(i)), SealDomain(s"$i"))
  }

  private lazy val maxedListItems = range.map {
    i =>
      ListItem(
        name = s"$i",
        changeUrl = routes.SealIdentityController.onPageLoad(mrn, eventIndex, Index(i), mode).url,
        removeUrl = routes.ConfirmRemoveSealController.onPageLoad(mrn, eventIndex, Index(i), mode).url
      )
  }

  "AddSeal Controller" - {

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {

        val allowMoreSeals = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addSealRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddSealView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreSeals), mrn, eventIndex, mode, (_, _) => Nil, _ => allowMoreSeals)(request, messages).toString
      }

      "when max limit reached" in {

        val allowMoreSeals = false

        setExistingUserAnswers(maxedUserAnswers)

        val request = FakeRequest(GET, addSealRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddSealView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMoreSeals), mrn, eventIndex, mode, (_, _) => maxedListItems, _ => allowMoreSeals)(request, messages).toString
      }
    }

    "must redirect to the next page" - {
      "when valid data is submitted and max limit not reached" in {

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addSealRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        verify(mockSessionRepository, never()).set(any())
      }

      "when max limit reached" in {

        when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

        setExistingUserAnswers(maxedUserAnswers)

        val request = FakeRequest(POST, addSealRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        verify(mockSessionRepository, never()).set(any())
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {

        val allowMoreSeals = true

        setExistingUserAnswers(emptyUserAnswers)

        val request   = FakeRequest(POST, addSealRoute).withFormUrlEncodedBody(("value", ""))
        val boundForm = form(allowMoreSeals).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddSealView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, eventIndex, mode, (_, _) => Nil, _ => allowMoreSeals)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addSealRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addSealRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
