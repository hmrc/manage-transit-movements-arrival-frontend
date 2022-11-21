package controllers.incident.equipment.itemNumber

import base.{SpecBase, AppWithDefaultMockFixtures}
import forms.YesNoFormProvider
import models.{NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.Identification
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.incident.equipment.itemNumber.ConfirmRemoveItemNumberPage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.incident.equipment.itemNumber.ConfirmRemoveItemNumberView
import navigation.{IdentificationNavigatorProvider, Navigator}

import scala.concurrent.Future

class ConfirmRemoveItemNumberControllerSpec extends SpecBase with AppWithDefaultMockFixtures with MockitoSugar {

  private val formProvider = new YesNoFormProvider()
  private val form         = formProvider("incident.equipment.itemNumber.confirmRemoveItemNumber")
  private val mode         = NormalMode
  private lazy val confirmRemoveItemNumberRoute = routes.ConfirmRemoveItemNumberController.onPageLoad(mrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[IdentificationNavigatorProvider]).toInstance(fakeIdentificationNavigatorProvider))

  "ConfirmRemoveItemNumber Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, confirmRemoveItemNumberRoute)
      val result = route(app, request).value

      val view = injector.instanceOf[ConfirmRemoveItemNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.setValue(ConfirmRemoveItemNumberPage, true)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, confirmRemoveItemNumberRoute)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> "true"))

      val view = injector.instanceOf[ConfirmRemoveItemNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, confirmRemoveItemNumberRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, confirmRemoveItemNumberRoute).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[ConfirmRemoveItemNumberView]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, confirmRemoveItemNumberRoute)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(POST, confirmRemoveItemNumberRoute)
        .withFormUrlEncodedBody(("value", "true"))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
