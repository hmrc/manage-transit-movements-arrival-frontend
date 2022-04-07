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

import models.requests.{SpecificDataRequestProvider, SpecificDataRequestProvider2}
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import queries.Gettable

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SpecificDataRequiredActionImpl2 @Inject() (implicit val ec: ExecutionContext) extends SpecificDataRequiredActionProvider2 {

  override def apply[T1, T2](page: Gettable[T2])(implicit
    rds: Reads[T2]
  ): ActionRefiner[SpecificDataRequestProvider[T1]#SpecificDataRequest, SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest] =
    new SpecificDataRequiredAction2(page)
}

trait SpecificDataRequiredActionProvider2 {

  def apply[T1, T2](page: Gettable[T2])(implicit
    rds: Reads[T2]
  ): ActionRefiner[SpecificDataRequestProvider[T1]#SpecificDataRequest, SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest]
}

class SpecificDataRequiredAction2[T1, T2](
  page: Gettable[T2]
)(implicit val executionContext: ExecutionContext, rds: Reads[T2])
    extends ActionRefiner[SpecificDataRequestProvider[T1]#SpecificDataRequest, SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest] {

  override protected def refine[A](
    request: SpecificDataRequestProvider[T1]#SpecificDataRequest[A]
  ): Future[Either[Result, SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest[A]]] =
    Future.successful {
      request.userAnswers.get(page) match {
        case Some(value) =>
          Right(new SpecificDataRequestProvider2[T1, T2].SpecificDataRequest(request, request.eoriNumber, request.userAnswers, value))
        case None =>
          Left(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
      }
    }

}
