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

package models

import models.identification.ProcedureType.Simplified
import models.locationOfGoods.TypeOfLocation.{ApprovedPlace, DesignatedLocation, Other}
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.TypeOfLocationPage

sealed trait QualifierOfIdentification extends Radioable[QualifierOfIdentification] {
  override val messageKeyPrefix: String = QualifierOfIdentification.messageKeyPrefix
  val code: String
}

object QualifierOfIdentification extends EnumerableType[QualifierOfIdentification] {

  case object CustomsOffice extends WithName("customsOffice") with QualifierOfIdentification {
    override val code: String = "V"
  }

  case object EoriNumber extends WithName("eoriNumber") with QualifierOfIdentification {
    override val code: String = "X"
  }

  case object AuthorisationNumber extends WithName("authorisationNumber") with QualifierOfIdentification {
    override val code: String = "Y"
  }

  case object Coordinates extends WithName("coordinates") with QualifierOfIdentification {
    override val code: String = "W"
  }

  case object Unlocode extends WithName("unlocode") with QualifierOfIdentification {
    override val code: String = "U"
  }

  case object Address extends WithName("address") with QualifierOfIdentification {
    override val code: String = "Z"
  }

  case object PostalCode extends WithName("postalCode") with QualifierOfIdentification {
    override val code: String = "T"
  }

  val messageKeyPrefix: String = "qualifierOfIdentification"

  def values(userAnswers: UserAnswers): Seq[QualifierOfIdentification] =
    userAnswers.get(TypeOfLocationPage) match {
      case Some(DesignatedLocation) => Seq(CustomsOffice, Unlocode)
      case Some(ApprovedPlace)      => Seq(PostalCode, Unlocode, Coordinates, EoriNumber, Address)
      case Some(Other)              => Seq(PostalCode, Unlocode, Coordinates, Address)
    }

  val values: Seq[QualifierOfIdentification] = Seq(
    CustomsOffice,
    EoriNumber,
    AuthorisationNumber,
    Coordinates,
    Unlocode,
    Address,
    PostalCode
  )

  val locationValues: Seq[QualifierOfIdentification] = Seq(
    Unlocode,
    Coordinates,
    Address
  )
}
