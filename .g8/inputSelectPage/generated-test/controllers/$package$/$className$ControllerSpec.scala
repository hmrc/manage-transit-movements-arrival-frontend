package controllers.$package$

import base.{SpecBase, AppWithDefaultMockFixtures}
import forms.$formProvider$
import views.html.$package$.$className$View
import models.{$referenceListClass$, NormalMode, UserAnswers}
import generators.Generators
import navigation.Navigator
import navigation.annotations.$navRoute$
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.$package$.$className$Page
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.$serviceName$

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val $referenceClass;format="decap"$1 = arbitrary$referenceClass$.arbitrary.sample.get
  private val $referenceClass;format="decap"$2 = arbitrary$referenceClass$.arbitrary.sample.get
  private val $referenceListClass;format="decap"$ = $referenceListClass$(Seq($referenceClass;format="decap"$1, $referenceClass;format="decap"$2))

  private val formProvider = new $formProvider$()
  private val form         = formProvider("$package$.$className;format="decap"$", $referenceListClass;format="decap"$)
  private val mode         = NormalMode

  private val mock$serviceName$: $serviceName$ = mock[$serviceName$]
  private lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(mrn, mode).url

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[Navigator]).qualifiedWith(classOf[$navRoute$]).toInstance(fakeNavigator))
      .overrides(bind(classOf[$serviceName$]).toInstance(mock$serviceName$))

  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {
      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceListClass;format="decap"$))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, $referenceListClass;format="decap"$.$referenceClassPlural;format="decap"$, mode)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {
      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceListClass;format="decap"$))
      val userAnswers = UserAnswers(mrn, eoriNumber).set($className$Page, $referenceClass;format="decap"$1).success.value
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val filledForm = form.bind(Map("value" -> $referenceClass;format="decap"$1.id))

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, $referenceListClass;format="decap"$.$referenceClassPlural;format="decap"$, mode)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {
      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceListClass;format="decap"$))
      when(mockSessionRepository.set(any())) thenReturn Future.successful(true)

      setExistingUserAnswers(emptyUserAnswers)

      val request =
        FakeRequest(POST, $className;format="decap"$Route)
      .withFormUrlEncodedBody(("value", $referenceClass;format="decap"$1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {
      when(mock$serviceName$.$lookupReferenceListMethod$(any())).thenReturn(Future.successful($referenceListClass;format="decap"$))
      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = form.bind(Map("value" -> "invalid value"))

      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, mrn, $referenceListClass;format="decap"$.$referenceClassPlural;format="decap"$, mode)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {
      setNoExistingUserAnswers()

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to Session Expired for a POST if no existing data is found" in {
      setNoExistingUserAnswers()

      val request =
        FakeRequest(POST, $className;format="decap"$Route)
      .withFormUrlEncodedBody(("value", $referenceClass;format="decap"$1.id))

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
