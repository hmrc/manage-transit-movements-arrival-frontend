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
import models.GoodsLocation
import models.reference.CustomsOffice
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.{CustomsOfficePage, GoodsLocationPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.ArrivalCompleteView

import scala.concurrent.Future

class ConfirmationControllerSpec extends SpecBase with AppWithDefaultMockFixtures {

  private lazy val confirmRoute = routes.ConfirmationController.onPageLoad(mrn).url

  "Confirmation Controller" - {

    "return OK and the correct view when there is no phone number for a GET then remove data" in {

      val customsOffice = CustomsOffice("id", Some("name"), None)
      val userAnswers = emptyUserAnswers
        .set(GoodsLocationPage, GoodsLocation.AuthorisedConsigneesLocation)
        .success
        .value
        .set(CustomsOfficePage, customsOffice)
        .success
        .value
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.remove(any(), any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, confirmRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ArrivalCompleteView]

      status(result) mustEqual OK

      verify(mockSessionRepository, times(1)).remove(mrn.toString, eoriNumber)

      val expectedParagraph = "and find out when unloading permission has been granted."
      val expectedContact   = "If the goods are not released or you have another problem, contact Customs at name."

      contentAsString(result) mustEqual
        view(mrn, expectedParagraph, expectedContact)(request, messages).toString
    }

    "return OK and the correct view when there is phone number for a GET the data" in {

      val customsOffice = CustomsOffice("id", Some("name"), Some("phoneNumber"))
      val userAnswers = emptyUserAnswers
        .set(GoodsLocationPage, GoodsLocation.BorderForceOffice)
        .success
        .value
        .set(CustomsOfficePage, customsOffice)
        .success
        .value

      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.remove(any(), any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, confirmRoute)
      val result  = route(app, request).value

      val view = injector.instanceOf[ArrivalCompleteView]

      status(result) mustEqual OK

      verify(mockSessionRepository, times(1)).remove(mrn.toString, eoriNumber)

      val expectedParagraph = "and find out when the goods have been released."
      val expectedContact   = "If the goods are not released or you have another problem, contact Customs at name, telephone phoneNumber."

      contentAsString(result) mustEqual
        view(mrn, expectedParagraph, expectedContact)(request, messages).toString
    }

    "return OK and the correct view when there is no phone number and office name for a GET the data" in {

      val customsOffice = CustomsOffice("id", None, None)
      val userAnswers = emptyUserAnswers
        .set(GoodsLocationPage, GoodsLocation.BorderForceOffice)
        .success
        .value
        .set(CustomsOfficePage, customsOffice)
        .success
        .value
      setExistingUserAnswers(userAnswers)

      when(mockSessionRepository.remove(any(), any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, routes.ConfirmationController.onPageLoad(mrn).url)
      val result  = route(app, request).value

      val view = injector.instanceOf[ArrivalCompleteView]

      status(result) mustEqual OK

      verify(mockSessionRepository, times(1)).remove(mrn.toString, eoriNumber)

      val expectedParagraph = "and find out when the goods have been released."
      val expectedContact   = "If the goods are not released or you have another problem, contact Customs at the supervising office (id)."

      contentAsString(result) mustEqual
        view(mrn, expectedParagraph, expectedContact)(request, messages).toString
    }
  }
}
