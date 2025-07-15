/*
 * Copyright 2025 HM Revenue & Customs
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
import connectors.SubmissionConnector
import models.{ArrivalMessage, ArrivalMessages}
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito.{reset, when}
import play.api.http.Status.OK
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HttpResponse

import java.time.LocalDateTime
import scala.concurrent.Future

class SubmissionServiceSpec extends SpecBase with AppWithDefaultMockFixtures {

  private val mockConnector = mock[SubmissionConnector]

  override def guiceApplicationBuilder(): GuiceApplicationBuilder =
    super
      .guiceApplicationBuilder()
      .overrides(bind(classOf[SubmissionConnector]).toInstance(mockConnector))

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockConnector)
  }

  "SubmissionService" - {

    "post" - {
      "must pass the mrn to the connector" in {
        when(mockConnector.post(eqTo(mrn))(any()))
          .thenReturn(Future.successful(HttpResponse(OK, "")))

        val service = app.injector.instanceOf[SubmissionService]

        val result = service.post(mrn).futureValue

        result.status mustEqual OK
      }
    }

    "getMessages" - {
      "must return the messages ordered by most recent received date" in {
        val now      = LocalDateTime.now()
        val message1 = ArrivalMessage("IE007", now.minusDays(2))
        val message2 = ArrivalMessage("IE057", now.minusDays(1))
        val message3 = ArrivalMessage("IE007", now)
        val messages = ArrivalMessages(Seq(message1, message2, message3))

        when(mockConnector.getMessages(eqTo(mrn))(any()))
          .thenReturn(Future.successful(messages))

        val service = app.injector.instanceOf[SubmissionService]

        val result = service.getMessages(mrn).futureValue

        result mustEqual Seq(message3, message2, message1)
      }
    }
  }
}
