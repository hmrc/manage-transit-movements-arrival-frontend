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

package controllers.incident.equipment

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddAnotherItemFormProvider
import generators.Generators
import models.incident.IncidentCode
import models.{Index, NormalMode, UserAnswers}
import navigation.IncidentNavigatorProvider
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.incident.equipment.ContainerIdentificationNumberYesNoPage
import pages.incident.{ContainerIndicatorYesNoPage, IncidentCodePage}
import pages.sections.incident.EquipmentSection
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewModels.ListItem
import viewModels.incident.AddAnotherEquipmentViewModel
import viewModels.incident.AddAnotherEquipmentViewModel.AddAnotherEquipmentViewModelProvider
import views.html.incident.equipment.AddAnotherEquipmentView

class AddAnotherEquipmentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val formProvider = new AddAnotherItemFormProvider()

  private def form(viewModel: AddAnotherEquipmentViewModel) =
    formProvider(viewModel.prefix, viewModel.allowMoreEquipments)

  private val mode = NormalMode

  private lazy val addAnotherSealRoute = routes.AddAnotherEquipmentController.onPageLoad(mrn, mode, incidentIndex).url

  private val mockViewModelProvider = mock[AddAnotherEquipmentViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[AddAnotherEquipmentViewModelProvider]).toInstance(mockViewModelProvider))
      .overrides(bind(classOf[IncidentNavigatorProvider]).toInstance(fakeIncidentNavigatorProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxTransportEquipments - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxTransportEquipments)(listItem)

  private val viewModel = arbitrary[AddAnotherEquipmentViewModel].sample.value

  private val viewModelWithNoItems          = viewModel.copy(listItems = Nil)
  private val viewModelWithItemsNotMaxedOut = viewModel.copy(listItems = listItems)
  private val viewModelWithItemsMaxedOut    = viewModel.copy(listItems = maxedOutListItems)

  "AddAnotherSeal Controller" - {

    "must remove any in progress transport equipments" in {
      val userAnswers = emptyUserAnswers
        .setValue(ContainerIdentificationNumberYesNoPage(incidentIndex, equipmentIndex), true)

      when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
        .thenReturn(viewModelWithItemsNotMaxedOut)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, addAnotherSealRoute)

      val result = route(app, request).value

      status(result) mustEqual OK

      val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
      verify(mockViewModelProvider).apply(userAnswersCaptor.capture(), any(), any())(any(), any())
      userAnswersCaptor.getValue.get(EquipmentSection(incidentIndex, equipmentIndex)) mustNot be(defined)
    }

    "must redirect to add seal yes/no page" - {
      "when 0 seals" in {
        when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
          .thenReturn(viewModelWithNoItems)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherSealRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          controllers.incident.routes.AddTransportEquipmentController.onPageLoad(mrn, mode, incidentIndex).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {
        when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
          .thenReturn(viewModelWithItemsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherSealRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherEquipmentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithItemsNotMaxedOut), mrn, viewModelWithItemsNotMaxedOut)(request, messages, frontendAppConfig).toString
      }

      "when max limit reached" in {
        when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
          .thenReturn(viewModelWithItemsMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherSealRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherEquipmentView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(viewModelWithItemsMaxedOut), mrn, viewModelWithItemsMaxedOut)(request, messages, frontendAppConfig).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "and container indicator is undefined" - {
          "must redirect to add container id yes/no at next index" in {
            forAll(Gen.oneOf(IncidentCode.SealsBrokenOrTampered, IncidentCode.PartiallyOrFullyUnloaded)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)

                when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
                  .thenReturn(viewModelWithItemsNotMaxedOut)

                val nextIndex = Index(viewModelWithItemsNotMaxedOut.numberOfTransportEquipments)

                setExistingUserAnswers(userAnswers)

                val request = FakeRequest(POST, addAnotherSealRoute)
                  .withFormUrlEncodedBody(("value", "true"))

                val result = route(app, request).value

                status(result) mustEqual SEE_OTHER

                redirectLocation(result).value mustEqual
                  routes.ContainerIdentificationNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, nextIndex).url
            }
          }
        }

        "and container indicator is true" - {
          "must redirect to container id at next index" in {
            forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), true)

                when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
                  .thenReturn(viewModelWithItemsNotMaxedOut)

                val nextIndex = Index(viewModelWithItemsNotMaxedOut.numberOfTransportEquipments)

                setExistingUserAnswers(userAnswers)

                val request = FakeRequest(POST, addAnotherSealRoute)
                  .withFormUrlEncodedBody(("value", "true"))

                val result = route(app, request).value

                status(result) mustEqual SEE_OTHER

                redirectLocation(result).value mustEqual
                  routes.ContainerIdentificationNumberController.onPageLoad(userAnswers.mrn, mode, incidentIndex, nextIndex).url
            }
          }
        }

        "and container indicator is false" - {
          "must redirect to add container id yes/no at next index" in {
            forAll(Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)) {
              incidentCode =>
                val userAnswers = emptyUserAnswers
                  .setValue(IncidentCodePage(incidentIndex), incidentCode)
                  .setValue(ContainerIndicatorYesNoPage(incidentIndex), false)

                when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
                  .thenReturn(viewModelWithItemsNotMaxedOut)

                val nextIndex = Index(viewModelWithItemsNotMaxedOut.numberOfTransportEquipments)

                setExistingUserAnswers(userAnswers)

                val request = FakeRequest(POST, addAnotherSealRoute)
                  .withFormUrlEncodedBody(("value", "true"))

                val result = route(app, request).value

                status(result) mustEqual SEE_OTHER

                redirectLocation(result).value mustEqual
                  routes.ContainerIdentificationNumberYesNoController.onPageLoad(userAnswers.mrn, mode, incidentIndex, nextIndex).url
            }
          }
        }
      }

      "when no submitted" - {
        "must redirect to next page" in {
          when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
            .thenReturn(viewModelWithItemsNotMaxedOut)

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
        when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
          .thenReturn(viewModelWithItemsMaxedOut)

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
        when(mockViewModelProvider.apply(any(), any(), any())(any(), any()))
          .thenReturn(viewModelWithItemsNotMaxedOut)

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherSealRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(viewModelWithItemsNotMaxedOut).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherEquipmentView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, viewModelWithItemsNotMaxedOut)(request, messages, frontendAppConfig).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherSealRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherSealRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
