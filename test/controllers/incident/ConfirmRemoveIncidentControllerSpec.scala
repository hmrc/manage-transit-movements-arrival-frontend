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

package controllers.incident

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.Generators
import models.{NormalMode, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.IncidentTextPage
import pages.sections.incident.IncidentSection
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.ConfirmRemoveIncidentView

class ConfirmRemoveIncidentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider                    = new YesNoFormProvider()
  private val form                            = formProvider("incident.remove", incidentIndex.display)
  private val mode                            = NormalMode
  private lazy val confirmRemoveIncidentRoute = routes.ConfirmRemoveIncidentController.onPageLoad(mrn, mode, incidentIndex).url

  private val incidentDescripton = Some(Gen.alphaStr.sample.value)

  "ConfirmRemoveIncident Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex)) {
        userAnswers =>
          setExistingUserAnswers(
            userAnswers
              .setValue(IncidentTextPage(incidentIndex), incidentDescripton.value)
          )

          val request = FakeRequest(GET, confirmRemoveIncidentRoute)
          val result  = route(app, request).value

          val view = injector.instanceOf[ConfirmRemoveIncidentView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(form, mrn, mode, incidentIndex, incidentDescripton)(request, messages).toString
      }
    }

    "must redirect to the next page when valid data is submitted" in {
      forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex)) {
        userAnswers =>
          beforeEach()

          setExistingUserAnswers(
            userAnswers
              .setValue(IncidentTextPage(incidentIndex), incidentDescripton.value)
          )

          val request =
            FakeRequest(POST, confirmRemoveIncidentRoute)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual routes.AddAnotherIncidentController.onPageLoad(mrn, mode).url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())(any())
          userAnswersCaptor.getValue.get(IncidentSection(incidentIndex)) mustNot be(defined)
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex)) {
        userAnswers =>
          setExistingUserAnswers(
            userAnswers
              .setValue(IncidentTextPage(incidentIndex), incidentDescripton.value)
          )

          val request   = FakeRequest(POST, confirmRemoveIncidentRoute).withFormUrlEncodedBody(("value", ""))
          val boundForm = form.bind(Map("value" -> ""))

          val result = route(app, request).value

          status(result) mustEqual BAD_REQUEST

          val view = injector.instanceOf[ConfirmRemoveIncidentView]

          contentAsString(result) mustEqual
            view(boundForm, mrn, mode, incidentIndex, incidentDescripton)(request, messages).toString
      }
    }

    "must redirect for a GET" - {
      "no existing data is found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(GET, confirmRemoveIncidentRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
      }

      "no incident is found" in {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, confirmRemoveIncidentRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.AddAnotherIncidentController.onPageLoad(mrn, mode).url
      }
    }

    "must redirect for a POST" - {
      "when no existing data is found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(POST, confirmRemoveIncidentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
      }

      "when no incident is found" in {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, confirmRemoveIncidentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual routes.AddAnotherIncidentController.onPageLoad(mrn, mode).url
      }
    }
  }
}
