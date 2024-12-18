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
import forms.AddAnotherItemFormProvider
import generators.Generators
import models.{Index, NormalMode}
import navigation.EquipmentNavigatorProvider
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.ListItem
import viewModels.incident.AddAnotherSealViewModel
import viewModels.incident.AddAnotherSealViewModel.AddAnotherSealViewModelProvider
import views.html.incident.equipment.seal.AddAnotherSealView

class AddAnotherSealControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val formProvider = new AddAnotherItemFormProvider()

  private def form(viewModel: AddAnotherSealViewModel) =
    formProvider(viewModel.prefix, viewModel.allowMoreSeals)

  private val mode = NormalMode

  private lazy val addAnotherSealRoute = routes.AddAnotherSealController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex).url

  private val mockViewModelProvider = mock[AddAnotherSealViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherSealViewModelProvider]).toInstance(mockViewModelProvider))
      .overrides(bind(classOf[EquipmentNavigatorProvider]).toInstance(fakeEquipmentNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxSeals - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxSeals)(listItem)

  private val viewModel = arbitrary[AddAnotherSealViewModel].sample.value

  private val viewModelWithNoSeals          = viewModel.copy(listItems = Nil)
  private val viewModelWithSealsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithSealsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  "AddAnotherSeal Controller" - {

    "redirect to add seal yes/no page" - {
      "when 0 seals" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
          .thenReturn(viewModelWithNoSeals)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherSealRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.incident.equipment.routes.AddSealsYesNoController.onPageLoad(mrn, mode, incidentIndex, equipmentIndex).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
          .thenReturn(viewModelWithSealsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherSealRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherSealView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithSealsNotMaxedOut), mrn, viewModelWithSealsNotMaxedOut)(request, messages, frontendAppConfig).toString
      }

      "when max limit reached" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
          .thenReturn(viewModelWithSealsMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherSealRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherSealView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithSealsMaxedOut), mrn, viewModelWithSealsMaxedOut)(request, messages, frontendAppConfig).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to seal id number page at next index" in {
          when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
            .thenReturn(viewModelWithSealsNotMaxedOut)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherSealRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.incident.equipment.seal.routes.SealIdentificationNumberController
              .onPageLoad(mrn, mode, incidentIndex, equipmentIndex, Index(listItems.length))
              .url
        }
      }

      "when no submitted" - {
        "must redirect to next page" in {
          when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
            .thenReturn(viewModelWithSealsNotMaxedOut)

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherSealRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "when max limit reached" - {
      "must redirect to next page" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
          .thenReturn(viewModelWithSealsMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherSealRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any(), any(), any())(any()))
          .thenReturn(viewModelWithSealsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherSealRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(viewModelWithSealsNotMaxedOut).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherSealView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, viewModelWithSealsNotMaxedOut)(request, messages, frontendAppConfig).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherSealRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherSealRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad(Some(mrn)).url
    }
  }
}
