package controllers.$package$

import base.{AppWithDefaultMockFixtures, SpecBase}
import forms.$formProvider$
import generators.Generators
import models.{UkAddress, NormalMode, UserAnswers}
import navigation.Navigator
import navigation.annotations.$navRoute$
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import pages.$package$.$className$Page
import $addressHolderNameImport$
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.$package$.$className$View

import scala.concurrent.Future

class $className$ControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val addressHolderName = Gen.alphaNumStr.sample.value

  private val testAddress = arbitrary[UkAddress].sample.value

  private val formProvider      = new $formProvider$()
  private val form              = formProvider("$package$.$className;format="decap"$", addressHolderName)

  private val mode                                 = NormalMode
  private lazy val $className;format="decap"$Route = routes.$className$Controller.onPageLoad(mrn, mode).url


  override def beforeEach(): Unit = {
    super.beforeEach()
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[$navRoute$NavigatorProvider]).toInstance(fake$navRoute$NavigatorProvider))


  "$className$ Controller" - {

    "must return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)
      val result = route(app, request).value

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, mrn, mode, addressHolderName)(request, messages).toString
    }

    "must populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(mrn, eoriNumber)
        .setValue($addressHolderNamePage$, addressHolderName)
        .setValue($className$Page, testAddress)

      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(GET, $className;format="decap"$Route)

      val result = route(app, request).value

      val filledForm = form.bind(
        Map(
          "addressLine1" -> testAddress.line1,
          "addressLine2" -> testAddress.line2,
          "postalCode"   -> testAddress.postalCode
        )
      )

      val view = injector.instanceOf[$className$View]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(filledForm, mrn, mode, addressHolderName)(request, messages).toString
    }

    "must redirect to the next page when valid data is submitted" in {

      when(mockSessionRepository.set(any())(any())) thenReturn Future.successful(true)

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(
          ("addressLine1", testAddress.line1),
          ("addressLine2", testAddress.line2),
          ("postalCode", testAddress.postalCode)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url
    }

    "must return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.setValue($addressHolderNamePage$, addressHolderName)
      setExistingUserAnswers(userAnswers)

      val request = FakeRequest(POST, $className;format="decap"$Route).withFormUrlEncodedBody(("value", ""))
      val boundForm = form.bind(Map("value" -> ""))

      val result = route(app, request).value

      status(result) mustEqual BAD_REQUEST

      val view = injector.instanceOf[$className$View]

      contentAsString(result) mustEqual
        view(boundForm, mrn, mode, addressHolderName)(request, messages).toString
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

      val request = FakeRequest(POST, $className;format="decap"$Route)
        .withFormUrlEncodedBody(
          ("addressLine1", testAddress.line1),
          ("addressLine2", testAddress.line2),
          ("postalCode", testAddress.postalCode)
        )

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
    }
  }
}
