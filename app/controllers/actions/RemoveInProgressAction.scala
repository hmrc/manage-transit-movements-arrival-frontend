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

import models.journeyDomain.{JourneyDomainModel, UserAnswersReader}
import models.requests.DataRequest
import models.{Index, RichJsArray}
import pages.sections.Section
import play.api.Logging
import play.api.libs.json.{JsArray, JsObject}
import play.api.mvc.{ActionRefiner, Result}
import repositories.SessionRepository
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveInProgressAction[T <: JourneyDomainModel](
  array: Section[JsArray],
  indexedValue: Index => Section[JsObject]
)(sessionRepository: SessionRepository)(implicit val executionContext: ExecutionContext, userAnswersReader: Index => UserAnswersReader[T])
    extends ActionRefiner[DataRequest, DataRequest]
    with Logging {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, DataRequest[A]]] = {
    val userAnswers = request.userAnswers
    userAnswers.get(array) match {
      case Some(value) =>
        val indexesToRemove = value
          .filterWithIndex {
            (_, i) => userAnswersReader(i).run(userAnswers).isLeft
          }
          .map(_._2)

        val updatedAnswers = indexesToRemove.reverse.foldLeft(userAnswers) {
          (acc, i) =>
            acc.remove(indexedValue(i)).getOrElse(acc)
        }

        implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
        sessionRepository.set(updatedAnswers).map {
          _ => Right(request.copy(userAnswers = updatedAnswers))
        }
      case _ =>
        Future.successful(Right(request))
    }
  }
}

trait RemoveInProgressActionProvider {

  def apply[T <: JourneyDomainModel](array: Section[JsArray], indexedValue: Index => Section[JsObject])(implicit
    userAnswersReader: Index => UserAnswersReader[T]
  ): ActionRefiner[DataRequest, DataRequest]
}

class RemoveInProgressActionProviderImpl @Inject() (sessionRepository: SessionRepository)(implicit val ec: ExecutionContext)
    extends RemoveInProgressActionProvider {

  override def apply[T <: JourneyDomainModel](array: Section[JsArray], indexedValue: Index => Section[JsObject])(implicit
    userAnswersReader: Index => UserAnswersReader[T]
  ): ActionRefiner[DataRequest, DataRequest] =
    new RemoveInProgressAction(array, indexedValue)(sessionRepository)
}
