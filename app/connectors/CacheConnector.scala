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

import config.FrontendAppConfig
import models.UserAnswers
import play.api.Logging
import play.api.http.Status._
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CacheConnector @Inject() (
  config: FrontendAppConfig,
  http: HttpClient
)(implicit ec: ExecutionContext)
    extends Logging {

  private val baseUrl = s"${config.cacheUrl}"

  def get(mrn: String)(implicit hc: HeaderCarrier): Future[Option[UserAnswers]] = {
    val url = s"$baseUrl/user-answers/$mrn"

    http.GET[UserAnswers](url).map {
      userAnswers => Some(userAnswers)
    } recover {
      case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
    }
  }

  def post(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/user-answers/${userAnswers.mrn}"

    http.POST[UserAnswers, HttpResponse](url, userAnswers).map(_.status == OK)
  }

  def checkLock(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/user-answers/${userAnswers.mrn}/lock"

    http
      .GET[HttpResponse](url)
      .map {
        _.status == OK
      }
  }

  def deleteLock(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/user-answers/${userAnswers.mrn}/lock"

    http
      .DELETE[HttpResponse](url)
      .map {
        _.status == OK
      }
  }

  def put(mrn: String)(implicit hc: HeaderCarrier): Future[Boolean] = {
    val url = s"$baseUrl/user-answers"

    http.PUT[String, HttpResponse](url, mrn).map {
      _.status == OK
    }
  }

}