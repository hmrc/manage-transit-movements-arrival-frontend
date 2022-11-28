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

import config.FrontendAppConfig
import models.reference.CustomsOffice
import models.{EoriNumber, Index, MovementReferenceNumber, UkAddress, UserAnswers}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.Injector
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.Aliases.{ActionItem, Content, Key, Value}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with ScalaCheckPropertyChecks
    with OptionValues
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with EitherValues {

  val eoriNumber: EoriNumber       = EoriNumber("GB123456")
  val mrn: MovementReferenceNumber = MovementReferenceNumber("19", "GB", "1234567890123")

  val emptyUserAnswers: UserAnswers = UserAnswers(mrn, eoriNumber, Json.obj())

  val authorisationIndex: Index = Index(0)
  val incidentIndex: Index      = Index(0)
  val containerIndex: Index     = Index(0)
  val sealIndex: Index          = Index(0)
  val index: Index              = Index(0)
  val equipmentIndex: Index     = Index(0)
  val itemNumberIndex: Index    = Index(0)

  val traderName: String       = "traderName"
  val consigneeName: String    = "consigneeName"
  val traderEoriNumber: String = "AB123456A"
  val customsOffice: String    = "customsOffice"

  val traderAddress: UkAddress    = UkAddress("buildingAndStreet", "city", "NE99 1XN")
  val consigneeAddress: UkAddress = UkAddress("buildingAndStreet", "city", "NE99 1XN")
  val configKey                   = "config"

  val officeOfDestination: CustomsOffice      = new CustomsOffice("ABC12345", Some("Test"), Some("+44 7760663422"))
  val officeOfDestinationNoTel: CustomsOffice = new CustomsOffice("ABC12345", Some("Test"), None)

  def injector: Injector                               = app.injector
  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  def messagesApi: MessagesApi    = injector.instanceOf[MessagesApi]
  implicit def messages: Messages = messagesApi.preferred(fakeRequest)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit def frontendAppConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]

  implicit class RichUserAnswers(userAnswers: UserAnswers) {

    def getValue[T](page: QuestionPage[T])(implicit rds: Reads[T]): T =
      userAnswers.get(page).value

    def setValue[T](page: QuestionPage[T], value: T)(implicit wts: Writes[T], rds: Reads[T]): UserAnswers =
      userAnswers.set(page, value).success.value

    def removeValue(page: QuestionPage[_]): UserAnswers =
      userAnswers.remove(page).success.value
  }

  implicit class RichContent(c: Content) {
    def value: String = c.asHtml.toString()
  }

  implicit class RichKey(k: Key) {
    def value: String = k.content.value
  }

  implicit class RichValue(v: Value) {
    def value: String = v.content.value
  }

  implicit class RichAction(ai: ActionItem) {
    def id: String = ai.attributes.get("id").value
  }

  def response(status: Int): Future[HttpResponse] = Future.successful(HttpResponse(status, ""))
}
