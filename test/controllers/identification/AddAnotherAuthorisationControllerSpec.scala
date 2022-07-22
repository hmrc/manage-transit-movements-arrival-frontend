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

package controllers.identification

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.AddItemFormProvider
import generators.{Generators, IdentificationUserAnswersGenerator}
import models.{Index, NormalMode}
import navigation.Navigator
import navigation.annotations.IdentificationDetails
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.hmrcfrontend.views.viewmodels.addtoalist.ListItem
import viewModels.identification.AddAnotherAuthorisationViewModel
import viewModels.identification.AddAnotherAuthorisationViewModel.AddAnotherAuthorisationViewModelProvider
import views.html.identification.AddAnotherAuthorisationView

class AddAnotherAuthorisationControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators with IdentificationUserAnswersGenerator {

  private val formProvider                  = new AddItemFormProvider()
  private def form(allowMoreItems: Boolean) = formProvider("identification.addAnotherAuthorisation", allowMoreItems)

  private lazy val addAnotherAuthorisationRoute = routes.AddAnotherAuthorisationController.onPageLoad(mrn).url

  private val mockViewModelProvider = mock[AddAnotherAuthorisationViewModelProvider]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[IdentificationDetails]).toInstance(fakeNavigator))
      .overrides(bind(classOf[AddAnotherAuthorisationViewModelProvider]).toInstance(mockViewModelProvider))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockViewModelProvider)
  }

  private val listItem          = arbitrary[ListItem].sample.value
  private val listItems         = Seq.fill(Gen.choose(1, frontendAppConfig.maxIdentificationAuthorisations - 1).sample.value)(listItem)
  private val maxedOutListItems = Seq.fill(frontendAppConfig.maxIdentificationAuthorisations)(listItem)

  "AddAnotherAuthorisation Controller" - {

    "redirect to add guarantee yes/no page" - {
      "when 0 guarantees" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherAuthorisationViewModel(Nil))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherAuthorisationRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual
          routes.IsSimplifiedProcedureController.onPageLoad(mrn, NormalMode).url
      }
    }

    "must return OK and the correct view for a GET" - {
      "when max limit not reached" in {
        val allowMore = true

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherAuthorisationViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherAuthorisationRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherAuthorisationView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMore), mrn, listItems, allowMore)(request, messages).toString
      }

      "when max limit reached" in {
        val allowMore = false

        val listItems = maxedOutListItems

        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherAuthorisationViewModel(listItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, addAnotherAuthorisationRoute)

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherAuthorisationView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form(allowMore), mrn, listItems, allowMore)(request, messages).toString
      }
    }

    "when max limit not reached" - {
      "when yes submitted" - {
        "must redirect to authorisation type page at next index" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherAuthorisationViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherAuthorisationRoute)
            .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual
            controllers.identification.authorisation.routes.AuthorisationTypeController.onPageLoad(mrn, Index(listItems.length), NormalMode).url
        }
      }

      "when no submitted" - {
        "must redirect to next page" in {
          when(mockViewModelProvider.apply(any())(any()))
            .thenReturn(AddAnotherAuthorisationViewModel(listItems))

          setExistingUserAnswers(emptyUserAnswers)

          val request = FakeRequest(POST, addAnotherAuthorisationRoute)
            .withFormUrlEncodedBody(("value", "false"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url
        }
      }
    }

    "when max limit reached" - {
      "must redirect to next page" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherAuthorisationViewModel(maxedOutListItems))

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherAuthorisationRoute)
          .withFormUrlEncodedBody(("value", ""))

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "must return a Bad Request and errors" - {
      "when invalid data is submitted and max limit not reached" in {
        when(mockViewModelProvider.apply(any())(any()))
          .thenReturn(AddAnotherAuthorisationViewModel(listItems))

        val allowMore = true

        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(POST, addAnotherAuthorisationRoute)
          .withFormUrlEncodedBody(("value", ""))

        val boundForm = form(allowMore).bind(Map("value" -> ""))

        val result = route(app, request).value

        val view = injector.instanceOf[AddAnotherAuthorisationView]

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, mrn, listItems, allowMore)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, addAnotherAuthorisationRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(POST, addAnotherAuthorisationRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
