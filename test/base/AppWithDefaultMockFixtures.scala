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

package base

import controllers.actions._
import models.{Index, Mode, UserAnswers}
import navigation._
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

import scala.concurrent.Future

trait AppWithDefaultMockFixtures extends BeforeAndAfterEach with GuiceOneAppPerSuite with GuiceFakeApplicationFactory with MockitoSugar {
  self: TestSuite =>

  override def beforeEach(): Unit = {
    reset(mockSessionRepository); reset(mockDataRetrievalActionProvider)

    when(mockSessionRepository.set(any())) thenReturn Future.successful(true)
  }

  final val mockSessionRepository: SessionRepository = mock[SessionRepository]
  final val mockDataRetrievalActionProvider          = mock[DataRetrievalActionProvider]

  final override def fakeApplication(): Application =
    guiceApplicationBuilder()
      .build()

  protected def setExistingUserAnswers(answers: UserAnswers): Unit =
    when(mockDataRetrievalActionProvider.apply(any())) thenReturn new FakeDataRetrievalAction(Some(answers))

  protected def setNoExistingUserAnswers(): Unit =
    when(mockDataRetrievalActionProvider.apply(any())) thenReturn new FakeDataRetrievalAction(None)

  protected val onwardRoute: Call = Call("GET", "/foo")

  protected val fakeNavigator: Navigator = new FakeNavigator(onwardRoute)

  protected val fakeIncidentNavigatorProvider: IncidentNavigatorProvider =
    (mode: Mode, index: Index) => new FakeIncidentNavigator(onwardRoute, index, mode)

  protected val fakeAuthorisationNavigatorProvider: AuthorisationNavigatorProvider =
    (mode: Mode, index: Index) => new FakeAuthorisationNavigator(onwardRoute, index, mode)

  protected val fakeAuthorisationsNavigatorProvider: AuthorisationsNavigatorProvider =
    (mode: Mode) => new FakeAuthorisationsNavigator(onwardRoute, mode)

  protected val fakeIdentificationNavigatorProvider: IdentificationNavigatorProvider =
    (mode: Mode) => new FakeIdentificationNavigator(onwardRoute, mode)

  protected val fakeLocationOfGoodsNavigatorProvider: LocationOfGoodsNavigatorProvider =
    (mode: Mode) => new FakeLocationOfGoodsNavigator(onwardRoute, mode)

  protected val fakeEquipmentNavigatorProvider: EquipmentNavigatorProvider =
    (mode: Mode, incidentIndex: Index, equipmentIndex: Index) => new FakeEquipmentNavigator(onwardRoute, incidentIndex, equipmentIndex, mode)

  protected val fakeSealNavigatorProvider: SealNavigatorProvider =
    (mode: Mode, incidentIndex: Index, equipmentIndex: Index, sealIndex: Index) =>
      new FakeSealNavigator(onwardRoute, incidentIndex, equipmentIndex, sealIndex, mode)

  protected val fakeArrivalNavigatorProvider: ArrivalNavigatorProvider =
    (mode: Mode) => new FakeArrivalNavigator(onwardRoute, mode)

  def guiceApplicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .overrides(
        bind[DataRequiredAction].to[DataRequiredActionImpl],
        bind[IdentifierAction].to[FakeIdentifierAction],
        bind[SessionRepository].toInstance(mockSessionRepository),
        bind[DataRetrievalActionProvider].toInstance(mockDataRetrievalActionProvider)
      )
}
