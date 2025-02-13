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

package base

import controllers.actions.*
import models.{LockCheck, Mode, UserAnswers}
import navigation.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call
import repositories.SessionRepository
import services.LockService

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite & SpecBase =>

  override def beforeEach(): Unit = {
    reset(mockSessionRepository); reset(mockDataRetrievalActionProvider); reset(mockLockService)

    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockLockService.checkLock(any())(any())).thenReturn(Future.successful(LockCheck.Unlocked))
  }

  final val mockSessionRepository: SessionRepository   = mock[SessionRepository]
  final val mockDataRetrievalActionProvider            = mock[DataRetrievalActionProvider]
  final val mockLockActionProvider: LockActionProvider = mock[LockActionProvider]
  final val mockLockService                            = mock[LockService]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(answers: UserAnswers): Unit = {
    when(mockLockActionProvider.apply()).thenReturn(new FakeLockAction(mockLockService))
    when(mockDataRetrievalActionProvider.apply(any())) `thenReturn` new FakeDataRetrievalAction(Some(answers))
  }

  protected def setNoExistingUserAnswers(): Unit = {
    when(mockLockActionProvider.apply()).thenReturn(new FakeLockAction(mockLockService))
    when(mockDataRetrievalActionProvider.apply(any())) `thenReturn` new FakeDataRetrievalAction(None)
  }

  protected val onwardRoute: Call = Call("GET", "/foo")

  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  protected val fakeArrivalNavigatorProvider: ArrivalNavigatorProvider =
    (mode: Mode) => new FakeArrivalNavigator(onwardRoute, mode)

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider),
        bind[LockService].toInstance(mockLockService),
        bind[LockActionProvider].toInstance(mockLockActionProvider)
      )
}
