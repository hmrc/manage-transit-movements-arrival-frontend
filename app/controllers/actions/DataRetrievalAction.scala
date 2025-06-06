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

package controllers.actions

import connectors.CacheConnector.IsTransitionalStateException
import models.MovementReferenceNumber
import models.requests.{IdentifierRequest, OptionalDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionProviderImpl @Inject() (sessionRepository: SessionRepository)(implicit ec: ExecutionContext) extends DataRetrievalActionProvider {

  def apply(mrn: MovementReferenceNumber): ActionRefiner[IdentifierRequest, OptionalDataRequest] =
    new DataRetrievalAction(mrn, sessionRepository)
}

trait DataRetrievalActionProvider {

  def apply(mrn: MovementReferenceNumber): ActionRefiner[IdentifierRequest, OptionalDataRequest]
}

class DataRetrievalAction(
  mrn: MovementReferenceNumber,
  sessionRepository: SessionRepository
)(implicit protected val executionContext: ExecutionContext)
    extends ActionRefiner[IdentifierRequest, OptionalDataRequest] {

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, OptionalDataRequest[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
    sessionRepository
      .get(mrn.toString)
      .map {
        userAnswers =>
          Right(OptionalDataRequest(request.request, request.eoriNumber, userAnswers))
      }
      .recover {
        case _: IsTransitionalStateException =>
          Left(Redirect(controllers.routes.DraftNoLongerAvailableController.onPageLoad()))
      }
  }
}
