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
import forms.events.seals.SealIdentityFormProvider
import generators.MessagesModelGenerators
import matchers.JsonMatchers
import models.domain.SealDomain
import models.messages.Seal
import models.{Index, NormalMode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import pages.events.seals.SealIdentityPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.viewmodels.NunjucksSupport
import views.html.events.seals.SealIdentityView

import scala.concurrent.Future

class SealIdentityControllerSpec extends SpecBase with AppWithDefaultMockFixtures with NunjucksSupport with JsonMatchers with MessagesModelGenerators {

  private val formProvider                                        = new SealIdentityFormProvider()
  private val form: Form[String]                                  = formProvider(sealIndex)
  private val mode                                                = NormalMode
  private def sealIdentityRoute(index: Index = sealIndex): String = routes.SealIdentityController.onPageLoad(mrn, eventIndex, index, mode).url

  "SealIdentity Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, sealIdentityRoute())
      val result  = route(app, request).value

      val view = injector.instanceOf[SealIdentityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, eventIndex, sealIndex, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      val userAnswers = emptyUserAnswers.set(SealIdentityPage(eventIndex, sealIndex), sealDomain).success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, sealIdentityRoute())

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> seal.numberOrMark))

      val view = injector.instanceOf[SealIdentityView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, eventIndex, sealIndex, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, sealIdentityRoute())
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must redirect to the next page when a value that is the same as the previous is submitted within the same index" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      val seal        = arbitrary[Seal].sample.value
      val userAnswers = emptyUserAnswers.set(SealIdentityPage(eventIndex, sealIndex), sealDomain).success.value

      setExistingUserAnswers(userAnswers)

      val request =
        FakeRequest(POST, sealIdentityRoute())
          .withFormUrlEncodedBody(("value", seal.numberOrMark))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, sealIdentityRoute()).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      val view = injector.instanceOf[SealIdentityView]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, eventIndex, sealIndex, mode)(request, messages).toString
    }

    "must return a Bad Request and errors when an existing seal is submitted and index is different to current index" in {
      val seal        = arbitrary[SealDomain].sample.value
      val userAnswers = emptyUserAnswers.set(SealIdentityPage(eventIndex, sealIndex), seal).success.value

      val nextIndex = Index(1)
      setExistingUserAnswers(userAnswers)

      val request   = FakeRequest(POST, sealIdentityRoute(nextIndex)).withFormUrlEncodedBody(("value", seal.numberOrMark))
      val result    = route(app, request).value
      val boundForm = formProvider(nextIndex, List(seal)).bind(Map("value" -> seal.numberOrMark))

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[SealIdentityView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, eventIndex, nextIndex, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, sealIdentityRoute())

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, sealIdentityRoute())
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
