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

package models.journeyDomain.locationOfGoods

import cats.implicits._
import models.identification.ProcedureType
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.reference.TypeOfLocation
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.TypeOfLocationPage

case class LocationOfGoodsDomain(
  typeOfLocation: Option[TypeOfLocation],
  qualifierOfIdentificationDetails: QualifierOfIdentificationDomain
)

object LocationOfGoodsDomain {

  implicit val userAnswersReader: UserAnswersReader[LocationOfGoodsDomain] = (
    IsSimplifiedProcedurePage.filterOptionalDependent(_ == ProcedureType.Normal)(TypeOfLocationPage.reader),
    UserAnswersReader[QualifierOfIdentificationDomain]
  ).tupled.map((LocationOfGoodsDomain.apply _).tupled)

}
