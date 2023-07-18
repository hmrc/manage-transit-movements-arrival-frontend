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

package models.locationOfGoods

import models.identification.ProcedureType.Simplified
import models.{EnumerableType, Radioable, UserAnswers, WithName}
import pages.identification.IsSimplifiedProcedurePage

trait TypeOfLocation extends Radioable[TypeOfLocation] {
  override val messageKeyPrefix: String = TypeOfLocation.messageKeyPrefix
  val code: String
}

object TypeOfLocation extends EnumerableType[TypeOfLocation] {

  case object DesignatedLocation extends WithName("designatedLocation") with TypeOfLocation {
    override val code: String = "A"
  }

  case object AuthorisedPlace extends WithName("authorisedPlace") with TypeOfLocation {
    override val code: String = "B"
  }

  case object ApprovedPlace extends WithName("approvedPlace") with TypeOfLocation {
    override val code: String = "C"
  }

  case object Other extends WithName("other") with TypeOfLocation {
    override val code: String = "D"
  }

  val messageKeyPrefix: String = "locationOfGoods.typeOfLocation"

  val values: Seq[TypeOfLocation] = Seq(
    AuthorisedPlace,
    DesignatedLocation,
    ApprovedPlace,
    Other
  )

  def values(userAnswers: UserAnswers): Seq[TypeOfLocation] =
    userAnswers.get(IsSimplifiedProcedurePage) match {
      case Some(Simplified) => values
      case _                => values.filterNot(_ == AuthorisedPlace)
    }

}
