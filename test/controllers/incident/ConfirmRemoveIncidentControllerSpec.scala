package controllers.incident

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.YesNoFormProvider
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.Identification
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatestplus.mockito.MockitoSugar
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.ConfirmRemoveIncidentView
import navigation.{IdentificationNavigatorProvider, Navigator}
import org.mockito.ArgumentCaptor
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.sections.incident.IncidentSection

import scala.concurrent.Future

class ConfirmRemoveIncidentControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  private val formProvider                    = new YesNoFormProvider()
  private val form                            = formProvider("incident.remove", incidentIndex.display)
  private val mode                            = NormalMode
  private lazy val confirmRemoveIncidentRoute = routes.ConfirmRemoveIncidentController.onPageLoad(mrn, mode,incidentIndex).url


  "ConfirmRemoveIncident Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveIncidentRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveIncidentView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode, incidentIndex)(request, messages).toString
    }


    "must redirect to the next page when valid data is submitted" in {

      forAll(arbitraryIncidentAnswers(emptyUserAnswers, incidentIndex)) {

        userAnswers =>

          reset(mockSessionRepository)
          when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

          setExistingUserAnswers(userAnswers)

          val request =
            FakeRequest(POST, confirmRemoveIncidentRoute)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(app, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url

          val userAnswersCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(mockSessionRepository).set(userAnswersCaptor.capture())
          userAnswersCaptor.getValue.get(IncidentSection(incidentIndex)) mustNot be(defined)
      }
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request   = FakeRequest(POST, confirmRemoveIncidentRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveIncidentView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, incidentIndex)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveIncidentRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveIncidentRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
