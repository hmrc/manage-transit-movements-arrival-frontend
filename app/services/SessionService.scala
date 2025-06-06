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

package services

import models.MovementReferenceNumber
import play.api.mvc.{Request, Result}
import services.SessionService.key

class SessionService {

  def get[A <: Request[?]](implicit request: A): Option[String] =
    request.session.get(key)

  def set[A <: Request[?]](result: Result, lrn: MovementReferenceNumber)(implicit request: A): Result =
    result.addingToSession(key -> lrn.toString)(request)

  def remove[A <: Request[?]](result: Result)(implicit request: A): Result =
    result.removingFromSession(key)
}

object SessionService {

  val key = "MRN"
}
