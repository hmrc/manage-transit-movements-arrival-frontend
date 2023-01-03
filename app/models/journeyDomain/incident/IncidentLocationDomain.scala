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

package models.journeyDomain.incident

import cats.implicits._
import models.journeyDomain.{GettableAsReaderOps, UserAnswersReader}
import models.reference.UnLocode
import models.{Coordinates, DynamicAddress, Index, QualifierOfIdentification}
import pages.incident.location.{AddressPage, CoordinatesPage, QualifierOfIdentificationPage, UnLocodePage}

sealed trait IncidentLocationDomain {
  val code: String
}

object IncidentLocationDomain {

  def userAnswersReader(index: Index): UserAnswersReader[IncidentLocationDomain] =
    QualifierOfIdentificationPage(index).reader.flatMap {
      case QualifierOfIdentification.Coordinates =>
        UserAnswersReader[IncidentCoordinatesLocationDomain](IncidentCoordinatesLocationDomain.userAnswersReader(index)).widen[IncidentLocationDomain]
      case QualifierOfIdentification.Unlocode =>
        UserAnswersReader[IncidentUnLocodeLocationDomain](IncidentUnLocodeLocationDomain.userAnswersReader(index)).widen[IncidentLocationDomain]
      case QualifierOfIdentification.Address =>
        UserAnswersReader[IncidentAddressLocationDomain](IncidentAddressLocationDomain.userAnswersReader(index)).widen[IncidentLocationDomain]
      case _ => UserAnswersReader.fail(QualifierOfIdentificationPage(index))
    }

}

case class IncidentCoordinatesLocationDomain(coordinates: Coordinates) extends IncidentLocationDomain {
  override val code: String = "W"
}

object IncidentCoordinatesLocationDomain {

  def userAnswersReader(index: Index): UserAnswersReader[IncidentCoordinatesLocationDomain] =
    CoordinatesPage(index).reader.map(IncidentCoordinatesLocationDomain(_))
}

case class IncidentUnLocodeLocationDomain(unLocode: UnLocode) extends IncidentLocationDomain {
  override val code: String = "U"
}

object IncidentUnLocodeLocationDomain {

  def userAnswersReader(index: Index): UserAnswersReader[IncidentUnLocodeLocationDomain] =
    UnLocodePage(index).reader.map(IncidentUnLocodeLocationDomain(_))
}

case class IncidentAddressLocationDomain(address: DynamicAddress) extends IncidentLocationDomain {
  override val code: String = "Z"
}

object IncidentAddressLocationDomain {

  def userAnswersReader(index: Index): UserAnswersReader[IncidentAddressLocationDomain] =
    AddressPage(index).reader.map(IncidentAddressLocationDomain(_))
}
