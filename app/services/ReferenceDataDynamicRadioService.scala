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

import config.Constants.LocationType.*
import config.Constants.QualifierCode.*
import connectors.ReferenceDataConnector
import models.reference.{Identification, IncidentCode, QualifierOfIdentification, TypeOfLocation}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataDynamicRadioService @Inject() (
  referenceDataConnector: ReferenceDataConnector
)(implicit ec: ExecutionContext) {

  def getIncidentCodes()(implicit hc: HeaderCarrier): Future[Seq[IncidentCode]] =
    referenceDataConnector
      .getIncidentCodes()
      .map(_.resolve())
      .map(_.toSeq)

  def getTypesOfLocation()(implicit hc: HeaderCarrier): Future[Seq[TypeOfLocation]] =
    referenceDataConnector
      .getTypesOfLocation()
      .map(_.resolve())
      .map(_.toSeq)
      .map(_.filterNot(_.code == AuthorisedPlace))

  def getTransportIdentifications()(implicit hc: HeaderCarrier): Future[Seq[Identification]] =
    referenceDataConnector
      .getTransportIdentifications()
      .map(_.resolve())
      .map(_.toSeq)

  def getIdentifications(typeOfLocation: TypeOfLocation)(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] = {
    def filterQualifierOfIdentificationUserAnswers(
      qualifiersOfIdentification: Seq[QualifierOfIdentification]
    ): Seq[QualifierOfIdentification] =
      typeOfLocation.code match {
        case DesignatedLocation =>
          qualifiersOfIdentification.filter(_.isOneOf(UnlocodeCode, CustomsOfficeCode))
        case ApprovedPlace =>
          qualifiersOfIdentification.filter(_.isOneOf(UnlocodeCode, CoordinatesCode, EoriNumberCode, AddressCode))
        case Other =>
          qualifiersOfIdentification.filter(_.isOneOf(UnlocodeCode, CoordinatesCode, AddressCode))
        case _ => qualifiersOfIdentification
      }

    referenceDataConnector
      .getIdentifications()
      .map(_.resolve())
      .map(_.toSeq)
      .map(filterQualifierOfIdentificationUserAnswers)
  }

  def getIncidentIdentifications()(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    referenceDataConnector
      .getIncidentIdentifications()
      .map(_.resolve())
      .map(_.toSeq)

}
