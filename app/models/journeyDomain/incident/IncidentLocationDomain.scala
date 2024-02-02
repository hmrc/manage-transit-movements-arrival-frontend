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

import config.Constants.QualifierCode._
import models.journeyDomain.{GettableAsReaderOps, JourneyDomainModel, Read, UserAnswersReader}
import models.{Coordinates, DynamicAddress, Index}
import pages.incident.location.{AddressPage, CoordinatesPage, QualifierOfIdentificationPage, UnLocodePage}

sealed trait IncidentLocationDomain extends JourneyDomainModel

object IncidentLocationDomain {

  def userAnswersReader(index: Index): Read[IncidentLocationDomain] =
    QualifierOfIdentificationPage(index).reader.to {
      _.code match {
        case CoordinatesCode =>
          IncidentCoordinatesLocationDomain.userAnswersReader(index)
        case UnlocodeCode =>
          IncidentUnLocodeLocationDomain.userAnswersReader(index)
        case AddressCode =>
          IncidentAddressLocationDomain.userAnswersReader(index)
        case _ =>
          UserAnswersReader.error(QualifierOfIdentificationPage(index))
      }
    }

}

case class IncidentCoordinatesLocationDomain(coordinates: Coordinates) extends IncidentLocationDomain

object IncidentCoordinatesLocationDomain {

  def userAnswersReader(index: Index): Read[IncidentLocationDomain] =
    CoordinatesPage(index).reader.map(IncidentCoordinatesLocationDomain.apply)
}

case class IncidentUnLocodeLocationDomain(unLocode: String) extends IncidentLocationDomain

object IncidentUnLocodeLocationDomain {

  def userAnswersReader(index: Index): Read[IncidentLocationDomain] =
    UnLocodePage(index).reader.map(IncidentUnLocodeLocationDomain.apply)
}

case class IncidentAddressLocationDomain(address: DynamicAddress) extends IncidentLocationDomain

object IncidentAddressLocationDomain {

  def userAnswersReader(index: Index): Read[IncidentLocationDomain] =
    AddressPage(index).reader.map(IncidentAddressLocationDomain.apply)
}
