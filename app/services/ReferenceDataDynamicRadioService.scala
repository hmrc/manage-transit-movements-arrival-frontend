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
import models.UserAnswers
import models.identification.ProcedureType.Normal
import models.reference.{Identification, IncidentCode, QualifierOfIdentification, TypeOfLocation}
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.TypeOfLocationPage
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

  def getTypesOfLocation(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Seq[TypeOfLocation]] =
    userAnswers.get(IsSimplifiedProcedurePage) match {
      case Some(Normal) =>
        referenceDataConnector
          .getTypesOfLocation(userAnswers)
          .map(_.sortBy(_.`type`.toLowerCase))
      case _ =>
        referenceDataConnector
          .getTypesOfLocation(userAnswers)
          .map(_.sortBy(_.`type`.toLowerCase))
          .map(
            x => filterLocationUserAnswers(x)
          )
    }

  def getTransportIdentifications()(implicit hc: HeaderCarrier): Future[Seq[Identification]] =
    referenceDataConnector
      .getTransportIdentifications()
      .map(_.sortBy(_.`type`.toLowerCase))

  def getIdentifications(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    referenceDataConnector
      .getIdentifications()
      .map(_.sortBy(_.qualifier.toLowerCase))
      .map(
        x => filterQualifierOfIdentificationUserAnswers(userAnswers, x)
      )

  def getIncidentIdentifications()(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    referenceDataConnector
      .getIncidentIdentifications()

  def filterLocationUserAnswers(locationTypes: Seq[TypeOfLocation]): Seq[TypeOfLocation] =
    locationTypes.filterNot(
      x => x.code == "B"
    )

  def filterQualifierOfIdentificationUserAnswers(userAnswers: UserAnswers,
                                                 qualifiersOfIdentification: Seq[QualifierOfIdentification]
  ): Seq[QualifierOfIdentification] =
    userAnswers.get(TypeOfLocationPage).map(_.code) match {

      case Some(DesignatedLocation) =>
        qualifiersOfIdentification
          .filterNot(
            x => x.code == PostalCodeCode | x.code == CoordinatesCode | x.code == EoriNumberCode | x.code == AuthorisationNumberCode | x.code == AddressCode
          )
      case Some(ApprovedPlace) =>
        qualifiersOfIdentification
          .filterNot(
            x => x.code == CustomsOfficeCode | x.code == AuthorisationNumberCode
          )
      case Some(OtherLocation) =>
        qualifiersOfIdentification
          .filterNot(
            x => x.code == CustomsOfficeCode | x.code == EoriNumberCode | x.code == AuthorisationNumberCode
          )
      case _ => qualifiersOfIdentification
    }

}
