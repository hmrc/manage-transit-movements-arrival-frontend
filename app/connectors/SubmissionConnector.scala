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

package connectors

import config.{FrontendAppConfig, PhaseConfig}
import models.{ArrivalMessages, MovementReferenceNumber}
import play.api.Logging
import play.api.libs.json.Json
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}
import play.api.libs.ws.JsonBodyWritables._

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionConnector @Inject() (
  config: FrontendAppConfig,
  http: HttpClientV2,
  phaseConfig: PhaseConfig
)(implicit ec: ExecutionContext)
    extends Logging {

  private val baseUrl = s"${config.cacheUrl}"

  private val headers = Seq(
    "APIVersion" -> phaseConfig.values.apiVersion.toString
  )

  def post(mrn: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    val url = url"$baseUrl/declaration/submit"
    http
      .post(url)
      .setHeader(headers*)
      .withBody(Json.toJson(mrn))
      .execute[HttpResponse]
  }

  def getMessages(mrn: MovementReferenceNumber)(implicit hc: HeaderCarrier): Future[ArrivalMessages] = {
    val url = url"$baseUrl/messages/$mrn"
    http
      .get(url)
      .setHeader(headers*)
      .execute[ArrivalMessages]
  }
}
