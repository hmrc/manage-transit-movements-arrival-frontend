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

import models.domain.{ContainerDomain, SealDomain}
import models.messages.{Container, Seal}
import models.{Address, EoriNumber, Index, MovementReferenceNumber, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with ScalaCheckPropertyChecks
    with OptionValues
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar {

  val eoriNumber: EoriNumber       = EoriNumber("GB123456")
  val mrn: MovementReferenceNumber = MovementReferenceNumber("19", "GB", "1234567890123")

  val emptyUserAnswers: UserAnswers = UserAnswers(mrn, eoriNumber, Json.obj())

  val eventIndex: Index     = Index(0)
  val containerIndex: Index = Index(0)
  val sealIndex: Index      = Index(0)

  val seal: Seal                       = Seal("sealNumber")
  val sealDomain: SealDomain           = SealDomain("sealNumber")
  val container: Container             = Container("containerNumber")
  val domainContainer: ContainerDomain = ContainerDomain("containerNumber")

  val traderName: String       = "traderName"
  val consigneeName: String    = "consigneeName"
  val traderEoriNumber: String = "AB123456A"
  val customsOffice: String    = "customsOffice"
  val eventTitle: String       = "eventTitle"

  val traderAddress: Address    = Address("buildingAndStreet", "city", "NE99 1XN")
  val consigneeAddress: Address = Address("buildingAndStreet", "city", "NE99 1XN")
  val configKey                 = "config"

  def injector: Injector                               = app.injector
  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  def messagesApi: MessagesApi    = injector.instanceOf[MessagesApi]
  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  implicit class RichUserAnswers(userAnswers: UserAnswers) {

    def getValue[T](page: QuestionPage[T])(implicit rds: Reads[T]): T =
      userAnswers.get(page).value

    def setValue[T](page: QuestionPage[T], value: T)(implicit wts: Writes[T]): UserAnswers =
      userAnswers.set(page, value).success.value

    def removeValue(page: QuestionPage[_]): UserAnswers =
      userAnswers.remove(page).success.value
  }
}
