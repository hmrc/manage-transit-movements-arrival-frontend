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

package controllers.actions

import models.requests.{DataRequest, SpecificDataRequestProvider}
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import queries.Gettable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SpecificDataRequiredActionImpl @Inject() (implicit val ec: ExecutionContext) extends SpecificDataRequiredActionProvider {

  override def apply[T](page: Gettable[T])(implicit rds: Reads[T]): ActionRefiner[DataRequest, SpecificDataRequestProvider[T]#SpecificDataRequest] =
    new SpecificDataRequiredAction(page)
}

trait SpecificDataRequiredActionProvider {
  def apply[T](page: Gettable[T])(implicit rds: Reads[T]): ActionRefiner[DataRequest, SpecificDataRequestProvider[T]#SpecificDataRequest]
}

class SpecificDataRequiredAction[T](
  page: Gettable[T]
)(implicit val executionContext: ExecutionContext, rds: Reads[T])
    extends ActionRefiner[DataRequest, SpecificDataRequestProvider[T]#SpecificDataRequest] {

  override protected def refine[A](request: DataRequest[A]): Future[Either[Result, SpecificDataRequestProvider[T]#SpecificDataRequest[A]]] =
    Future.successful {
      request.userAnswers.get(page) match {
        case Some(value) =>
          Right(new SpecificDataRequestProvider[T].SpecificDataRequest(request, request.eoriNumber, request.userAnswers, value))
        case None =>
          Left(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
    }

}
