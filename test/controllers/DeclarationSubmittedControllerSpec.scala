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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.SubmissionConnector
import generators.Generators
import models.reference.CustomsOffice
import models.{ArrivalMessage, ArrivalMessages}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.DestinationOfficePage
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.DeclarationSubmittedView

import scala.concurrent.Future

class DeclarationSubmittedControllerSpec extends SpecBase with AppWithDefaultMockFixtures with ScalaCheckPropertyChecks with Generators {

  private val mockSubmissionConnector: SubmissionConnector = mock[SubmissionConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[SubmissionConnector]).toInstance(mockSubmissionConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockSubmissionConnector)
  }

  private lazy val declarationSubmittedRoute = routes.DeclarationSubmittedController.onPageLoad(mrn).url

  "Declaration Submitted Controller" - {

    "must return OK and the correct view for a GET and purge the cache" in {
      forAll(arbitrary[CustomsOffice]) {
        customsOffice =>
          beforeEach()

          when(mockSubmissionConnector.getMessages(any())(any()))
            .thenReturn(Future.successful(ArrivalMessages(Seq(ArrivalMessage("IE007")))))

          val userAnswers = emptyUserAnswers.setValue(DestinationOfficePage, customsOffice)
          setExistingUserAnswers(userAnswers)

          val request = FakeRequest(GET, declarationSubmittedRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[DeclarationSubmittedView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(mrn.toString, customsOffice)(request, messages).toString
      }
    }

    "must redirect to Session Expired for a GET" - {
      "when no existing data is found" in {
        setNoExistingUserAnswers()

        val request = FakeRequest(GET, declarationSubmittedRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }

      "when destination office is undefined" in {
        setExistingUserAnswers(emptyUserAnswers)

        val request = FakeRequest(GET, declarationSubmittedRoute)

        val result = route(app, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url
      }
    }
  }
}
