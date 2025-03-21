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

import connectors.SubmissionConnector
import models.{ArrivalMessage, MovementReferenceNumber}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import java.time.LocalDateTime
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionService @Inject() (
  connector: SubmissionConnector
)(implicit ec: ExecutionContext) {

  def post(mrn: MovementReferenceNumber)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    connector.post(mrn)

  def getMessages(mrn: MovementReferenceNumber)(implicit hc: HeaderCarrier): Future[Seq[ArrivalMessage]] = {
    implicit val ordering: Ordering[ArrivalMessage] = Ordering.by[ArrivalMessage, LocalDateTime](_.received).reverse
    connector.getMessages(mrn).map(_.messages.sorted)
  }
}
