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

package controllers

import base.{AppWithDefaultMockFixtures, SpecBase}
import models.CheckMode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalacheck.Gen
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ArrivalSubmissionService
import uk.gov.hmrc.http.HttpResponse
import utils.CheckYourAnswersHelper
import viewModels.sections.Section
import views.html.CheckYourAnswersView

import scala.concurrent.Future

class CheckYourAnswersControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  val mockService: ArrivalSubmissionService = mock[ArrivalSubmissionService]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind[ArrivalSubmissionService].toInstance(mockService))

  lazy val checkYourAnswersRoute = routes.CheckYourAnswersController.onPageLoad(mrn).url

  "Check Your Answers Controller" - {

    "must return OK and the correct view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val request = FakeRequest(GET, checkYourAnswersRoute)
      val view    = injector.instanceOf[CheckYourAnswersView]
      val result  = route(app, request).value

      status(result) mustEqual OK

      val helper = new CheckYourAnswersHelper(emptyUserAnswers, CheckMode)
      val sections = Seq(
        Section(
          Seq(helper.movementReferenceNumber)
        ),
        Section(
          messages("checkYourAnswers.section.goodsLocation"),
          Seq(helper.goodsLocation, helper.authorisedLocation, helper.customsSubPlace, helper.customsOffice).flatten
        ),
        Section(
          messages("checkYourAnswers.section.consigneeDetails"),
          Seq(helper.consigneeName, helper.eoriNumber, helper.consigneeAddress, helper.pickCustomsOffice).flatten
        ),
        Section(
          messages("checkYourAnswers.section.events"),
          helper.incidentOnRoute.toSeq
        )
      )

      contentAsString(result) mustEqual
        view(mrn, sections)(request, messages).toString
    }

    "must redirect to Session Expired for a GET if no existing data is found" in {

      setNoExistingUserAnswers()

      val request = FakeRequest(GET, routes.CheckYourAnswersController.onPageLoad(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "must redirect to 'Application Complete' page on valid submission" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockService.submit(any())(any())).thenReturn(Future.successful(Some(HttpResponse(ACCEPTED, ""))))

      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ConfirmationController.onPageLoad(mrn).url
    }

    "must fail with bad request error on invalid submission" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockService.submit(any())(any())).thenReturn(Future.successful(Some(HttpResponse(BAD_REQUEST, ""))))

      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.badRequest().url
    }

    "must redirected to TechnicalDifficulties page when there is a server side error" in {

      val genServerError: Int = Gen.chooseNum(500, 599).sample.value

      setExistingUserAnswers(emptyUserAnswers)

      when(mockService.submit(any())(any())).thenReturn(Future.successful(Some(HttpResponse.apply(genServerError, ""))))

      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }

    "must fail with internal server error when service fails" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockService.submit(any())(any())).thenReturn(Future.successful(None))

      val request = FakeRequest(POST, routes.CheckYourAnswersController.onSubmit(mrn).url)

      val result = route(app, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }
  }
}
