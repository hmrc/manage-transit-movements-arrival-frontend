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
import generators.Generators
import models.ArrivalId
import models.messages.ErrorType.{IncorrectValue, MRNError}
import models.messages.{ArrivalNotificationRejectionMessage, ErrorPointer, FunctionalError}
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalacheck.Arbitrary.arbitrary
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.ArrivalRejectionService
import views.html.{ArrivalGeneralRejectionView, MovementReferenceNumberRejectionView}

import java.time.LocalDate
import scala.concurrent.Future

class ArrivalRejectionControllerSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  private val mockArrivalRejectionService = mock[ArrivalRejectionService]

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockArrivalRejectionService)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[ArrivalRejectionService].toInstance(mockArrivalRejectionService)
      )

  private val arrivalId = ArrivalId(1)

  private lazy val rejectionRoute = routes.ArrivalRejectionController.onPageLoad(arrivalId).url

  "ArrivalRejection Controller" - {

    "must return OK and the correct view for a GET" in {
      forAll(arbitrary[FunctionalError], arbitrary[MRNError]) {
        (functionalError, mrnError) =>
          beforeEach()

          setExistingUserAnswers(emptyUserAnswers)

          val errors = Seq(functionalError.copy(errorType = mrnError))

          when(mockArrivalRejectionService.arrivalRejectionMessage(any())(any(), any()))
            .thenReturn(Future.successful(Some(ArrivalNotificationRejectionMessage(mrn.toString, LocalDate.now, None, None, errors))))

          val request = FakeRequest(GET, rejectionRoute)

          val result = route(app, request).value

          val view = injector.instanceOf[MovementReferenceNumberRejectionView]

          status(result) mustEqual OK

          verify(mockArrivalRejectionService, times(1)).arrivalRejectionMessage(eqTo(arrivalId))(any(), any())

          contentAsString(result) mustEqual
            view(arrivalId, mrnError, mrn.toString)(request, messages).toString
      }
    }

    "must return OK and the correct arrivalGeneralRejection view for a GET" in {

      setExistingUserAnswers(emptyUserAnswers)

      val errors = Seq(FunctionalError(IncorrectValue, ErrorPointer("TRD.TIN"), None, None))

      when(mockArrivalRejectionService.arrivalRejectionMessage(any())(any(), any()))
        .thenReturn(Future.successful(Some(ArrivalNotificationRejectionMessage(mrn.toString, LocalDate.now, None, None, errors))))

      val request = FakeRequest(GET, rejectionRoute)

      val result = route(app, request).value

      val view = injector.instanceOf[ArrivalGeneralRejectionView]

      status(result) mustEqual OK

      verify(mockArrivalRejectionService, times(1)).arrivalRejectionMessage(eqTo(arrivalId))(any(), any())

      contentAsString(result) mustEqual
        view(errors)(request, messages).toString
    }

    "render 'Technical difficulties' page when arrival rejection message is malformed" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockArrivalRejectionService.arrivalRejectionMessage(any())(any(), any()))
        .thenReturn(Future.successful(None))

      val request = FakeRequest(GET, rejectionRoute)

      val result = route(app, request).value

      verify(mockArrivalRejectionService, times(1)).arrivalRejectionMessage(eqTo(arrivalId))(any(), any())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.ErrorController.technicalDifficulties().url
    }
  }
}
