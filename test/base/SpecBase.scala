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

import models.{EoriNumber, Id, MovementReferenceNumber, UserAnswers}
import org.apache.pekko.stream.testkit.NoMaterializer
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OptionValues, TryValues}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.{AnyContentAsEmpty, BodyParsers}
import play.api.test.FakeRequest
import play.api.test.Helpers.stubPlayBodyParsers
import uk.gov.hmrc.govukfrontend.views.Aliases.{ActionItem, Content, Key, Value}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with OptionValues
    with TryValues
    with ScalaFutures
    with IntegrationPatience
    with MockitoSugar
    with EitherValues {

  val eoriNumber: EoriNumber = EoriNumber("GB123456")

  lazy val mrn: MovementReferenceNumber = new MovementReferenceNumber("51GBLFUWH7WOI085M4")

  lazy val emptyUserAnswers: UserAnswers = UserAnswers(mrn, eoriNumber, Json.obj(), None, Id())

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit class RichUserAnswers(userAnswers: UserAnswers) {

    def getValue[T](page: QuestionPage[T])(implicit rds: Reads[T]): T =
      userAnswers.get(page).value

    def setValue[T](page: QuestionPage[T], value: T)(implicit wts: Writes[T], rds: Reads[T]): UserAnswers =
      userAnswers.set(page, value).success.value

    def removeValue(page: QuestionPage[?]): UserAnswers =
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

  implicit val bodyParser: BodyParsers.Default = new BodyParsers.Default(stubPlayBodyParsers(NoMaterializer))

  def response(status: Int): Future[HttpResponse] = Future.successful(HttpResponse(status, ""))
}
