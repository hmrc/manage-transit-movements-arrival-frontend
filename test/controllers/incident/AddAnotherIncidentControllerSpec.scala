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
import forms.AddAnotherItemFormProvider
import generators.Generators
import models.reference.Country
import models.{Index, NormalMode, UserAnswers}
import navigation.ArrivalNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.incident.IncidentCountryPage
import pages.sections.incident.IncidentSection
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.ListItem
import viewModels.incident.AddAnotherIncidentViewModel
import viewModels.incident.AddAnotherIncidentViewModel.AddAnotherIncidentViewModelProvider
import views.html.incident.AddAnotherIncidentView

class AddAnotherIncidentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider = new AddAnotherItemFormProvider()

  private def form(viewModel: AddAnotherIncidentViewModel) =
    formProvider(viewModel.prefix, viewModel.allowMoreIncidents)

  private val mode = NormalMode

  private lazy val addAnotherIncidentRoute = routes.AddAnotherIncidentController.onPageLoad(mrn, mode).url

  private val mockViewModelProvider = mock[AddAnotherIncidentViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherIncidentViewModelProvider]).toInstance(mockViewModelProvider))
      .overrides(bind(classOf[ArrivalNavigatorProvider]).toInstance(fakeArrivalNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxIncidents - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxIncidents)(listItem)

  private val viewModel = arbitrary[AddAnotherIncidentViewModel].sample.value

  private val viewModelWithNoItems          = viewModel.copy(listItems = Nil)
  private val viewModelWithItemsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithItemsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  "AddAnotherIncident Controller" - {

    "must remove any in progress transport equipments" in {
      val userAnswers = emptyUserAnswers
        .setValue(IncidentCountryPage(incidentIndex), arbitrary[Country].sample.value)

      when(mockViewModelProvider.apply(any(), any())(any()))
        .thenReturn(viewModelWithItemsNotMaxedOut)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addAnotherIncidentRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockViewModelProvider).apply(userAnswersCaptor.capture(), any())(any())
      userAnswersCaptor.getValue.get(IncidentSection(incidentIndex)) mustNot be(defined)
    }

    "redirect to add incident yes/no" - {
      "when 0 incidents" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithNoItems)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherIncidentRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.incident.routes.IncidentFlagController.onPageLoad(mrn, mode).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithItemsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherIncidentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherIncidentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithItemsNotMaxedOut), mrn, viewModelWithItemsNotMaxedOut)(request, messages, frontendAppConfig).toString
      }

      "when max limit reached" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithItemsMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherIncidentRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherIncidentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithItemsMaxedOut), mrn, viewModelWithItemsMaxedOut)(request, messages, frontendAppConfig).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to incident country page at next index" in {
          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(viewModelWithItemsNotMaxedOut)

          setExistingUserAnswers(emptyUserAnswers)

          val nextIndex = Index(viewModelWithItemsNotMaxedOut.numberOfIncidents)

          val request = FakeRequest(POST, addAnotherIncidentRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            routes.IncidentCountryController.onPageLoad(mrn, mode, nextIndex).url
        }
      }

      "when no submitted" - {
        "must redirect to next page" in {
          when(mockViewModelProvider.apply(any(), any())(any()))
            .thenReturn(viewModelWithItemsNotMaxedOut)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherIncidentRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "when max limit reached" - {
      "must redirect to next page" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithItemsMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherIncidentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any())(any()))
          .thenReturn(viewModelWithItemsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherIncidentRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(viewModelWithItemsNotMaxedOut).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherIncidentView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, viewModelWithItemsNotMaxedOut)(request, messages, frontendAppConfig).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherIncidentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherIncidentRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
