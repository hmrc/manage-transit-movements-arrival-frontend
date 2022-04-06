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

import models.MovementReferenceNumber
import models.requests.{DataRequest, OptionalDataRequest}
import play.api.libs.json.Reads
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionBuilder, AnyContent, Result}
import queries.Gettable

import javax.inject.Inject
import scala.concurrent.Future

class Actions @Inject() (
  identifierAction: IdentifierAction,
  dataRetrievalActionProvider: DataRetrievalActionProvider,
  dataRequiredAction: DataRequiredAction
) {

  def getData(mrn: MovementReferenceNumber): ActionBuilder[OptionalDataRequest, AnyContent] =
    identifierAction andThen dataRetrievalActionProvider(mrn)

  def requireData(mrn: MovementReferenceNumber): ActionBuilder[DataRequest, AnyContent] =
    getData(mrn) andThen dataRequiredAction
}

object Actions {

  def getPage[T](page: Gettable[T])(block: T => Result)(implicit request: DataRequest[_], rds: Reads[T]): Result =
    getPage[T, Result](page)(block)(identity)

  def getPageF[T](page: Gettable[T])(block: T => Future[Result])(implicit request: DataRequest[_], rds: Reads[T]): Future[Result] =
    getPage[T, Future[Result]](page)(block)(Future.successful)

  private def getPage[T, R](page: Gettable[T])(block: T => R)(f: Result => R)(implicit request: DataRequest[_], rds: Reads[T]): R =
    request.userAnswers.get(page) match {
      case Some(value) => block(value)
      case None        => f(Redirect(controllers.routes.SessionExpiredController.onPageLoad()))
    }
}
