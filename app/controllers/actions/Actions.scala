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
import models.requests.{DataRequest, OptionalDataRequest, SpecificDataRequestProvider, SpecificDataRequestProvider2}
import play.api.libs.json.Reads
import play.api.mvc.{ActionBuilder, AnyContent}
import queries.Gettable

import javax.inject.Inject

class Actions @Inject() (
  identifierAction: IdentifierAction,
  dataRetrievalActionProvider: DataRetrievalActionProvider,
  dataRequiredAction: DataRequiredAction,
  specificDataRequiredActionProvider: SpecificDataRequiredActionProvider,
  specificDataRequiredActionProvider2: SpecificDataRequiredActionProvider2
) {

  def getData(mrn: MovementReferenceNumber): ActionBuilder[OptionalDataRequest, AnyContent] =
    identifierAction andThen dataRetrievalActionProvider(mrn)

  def requireData(mrn: MovementReferenceNumber): ActionBuilder[DataRequest, AnyContent] =
    getData(mrn) andThen dataRequiredAction

  def requireSpecificData[T](
    mrn: MovementReferenceNumber,
    page: Gettable[T]
  )(implicit rds: Reads[T]): ActionBuilder[SpecificDataRequestProvider[T]#SpecificDataRequest, AnyContent] =
    requireData(mrn) andThen specificDataRequiredActionProvider(page)

  def requireSpecificData2[T1, T2](
    mrn: MovementReferenceNumber,
    page1: Gettable[T1],
    page2: Gettable[T2]
  )(implicit rds1: Reads[T1], rds2: Reads[T2]): ActionBuilder[SpecificDataRequestProvider2[T1, T2]#SpecificDataRequest, AnyContent] =
    requireSpecificData(mrn, page1) andThen specificDataRequiredActionProvider2(page2)
}
