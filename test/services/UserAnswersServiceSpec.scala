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

package services

import base.{AppWithDefaultMockFixtures, SpecBase}
import connectors.ReferenceDataConnector
import generators.Generators
import models.{EoriNumber, UserAnswers}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Future

class UserAnswersServiceSpec extends SpecBase with AppWithDefaultMockFixtures with Generators {

  val mockReferenceDataConnector = mock[ReferenceDataConnector]

  override def beforeEach: Unit = {
    super.beforeEach()
    reset(mockReferenceDataConnector)
  }

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(
        bind[ReferenceDataConnector].toInstance(mockReferenceDataConnector)
      )

  "UserAnswers" - {
    "must return existing UserAnswers from repository if condition matches" in {

      setExistingUserAnswers(emptyUserAnswers)

      val eoriNumber          = EoriNumber("123")
      val existingUserAnswers = emptyUserAnswers.copy(eoriNumber = eoriNumber)
      when(mockSessionRepository.get(any(), any())) thenReturn Future.successful(Some(existingUserAnswers))

      val userAnswersService = app.injector.instanceOf[UserAnswersService]
      userAnswersService.getOrCreateUserAnswers(eoriNumber, mrn).futureValue mustBe existingUserAnswers
    }

    "must return basic UserAnswers with eori numbar and mrn if there is no record exist" in {

      setExistingUserAnswers(emptyUserAnswers)

      when(mockSessionRepository.get(any(), any())) thenReturn Future.successful(None)

      val userAnswersService  = app.injector.instanceOf[UserAnswersService]
      val result: UserAnswers = userAnswersService.getOrCreateUserAnswers(eoriNumber, mrn).futureValue

      result.eoriNumber mustBe emptyUserAnswers.eoriNumber
      result.mrn mustBe mrn
    }
  }
}
