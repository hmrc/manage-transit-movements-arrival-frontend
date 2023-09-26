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

import config.Constants._
import connectors.ReferenceDataConnector
import models.identification.ProcedureType
import models.identification.ProcedureType.Normal
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
      .map(_.sortBy(_.code.toLowerCase))

  def getTypesOfLocation()(implicit hc: HeaderCarrier): Future[Seq[TypeOfLocation]] = {
    def filter(typesOfLocation: Seq[TypeOfLocation]): Seq[TypeOfLocation] =
      typesOfLocation.filterNot(_.code == AuthorisedPlace)

    referenceDataConnector
      .getTypesOfLocation()
      .map(filter)
      .map(_.sortBy(_.`type`.toLowerCase))
  }

  def getTransportIdentifications()(implicit hc: HeaderCarrier): Future[Seq[Identification]] =
    referenceDataConnector
      .getTransportIdentifications()
      .map(_.sortBy(_.`type`.toLowerCase))

  def getIdentifications(typeOfLocation: TypeOfLocation)(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] = {
    def filterQualifierOfIdentificationUserAnswers(
      qualifiersOfIdentification: Seq[QualifierOfIdentification]
    ): Seq[QualifierOfIdentification] =
      typeOfLocation.code match {
        case DesignatedLocation =>
          qualifiersOfIdentification.filterNot(
            x => x.code == PostalCodeCode || x.code == CoordinatesCode || x.code == EoriNumberCode || x.code == AuthorisationNumberCode || x.code == AddressCode
          )
        case ApprovedPlace =>
          qualifiersOfIdentification.filterNot(
            x => x.code == CustomsOfficeCode || x.code == AuthorisationNumberCode
          )
        case Other =>
          qualifiersOfIdentification.filterNot(
            x => x.code == CustomsOfficeCode || x.code == EoriNumberCode || x.code == AuthorisationNumberCode
          )
        case _ => qualifiersOfIdentification
      }

    referenceDataConnector
      .getIdentifications()
      .map(_.sortBy(_.qualifier.toLowerCase))
      .map(
        filterQualifierOfIdentificationUserAnswers
      )
  }

  def getIncidentIdentifications()(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    referenceDataConnector
      .getIncidentIdentifications()

}
