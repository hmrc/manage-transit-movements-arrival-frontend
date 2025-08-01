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

import config.FrontendAppConfig
import controllers.actions.*
import models.{Mode, UserAnswers}
import navigation.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.{BeforeAndAfterEach, TestSuite}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.{GuiceFakeApplicationFactory, GuiceOneAppPerSuite}
import play.api.Application
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.{bind, Injector}
import play.api.mvc.{AnyContentAsEmpty, Call}
import play.api.test.FakeRequest
import repositories.SessionRepository

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite & SpecBase =>

  def injector: Injector = app.injector

  implicit def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  def messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  override def beforeEach(): Unit = {
    reset(mockSessionRepository)
    reset(mockDataRetrievalActionProvider)

    when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))
    when(mockLockActionProvider.apply()).thenReturn(new FakeLockAction())
  }

  final val mockSessionRepository: SessionRepository           = mock[SessionRepository]
  final private val mockDataRetrievalActionProvider            = mock[DataRetrievalActionProvider]
  final private val mockLockActionProvider: LockActionProvider = mock[LockActionProvider]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(answers: UserAnswers): Unit =
    when(mockDataRetrievalActionProvider.apply(any())) `thenReturn` new FakeDataRetrievalAction(Some(answers))

  protected def setNoExistingUserAnswers(): Unit =
    when(mockDataRetrievalActionProvider.apply(any())) `thenReturn` new FakeDataRetrievalAction(None)

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
        bind[LockActionProvider].toInstance(mockLockActionProvider)
      )
}
