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

import connectors.ReferenceDataConnector
import models.reference.{Identification, IncidentCode, QualifierOfIdentification, TypeOfLocation}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

// TODO - rename to ReferenceDataService or DynamicRadioService or something
class IncidentCodeService @Inject() (
  referenceDataConnector: ReferenceDataConnector
)(implicit ec: ExecutionContext) {

  def getIncidentCodes()(implicit hc: HeaderCarrier): Future[Seq[IncidentCode]] =
    referenceDataConnector
      .getIncidentCodes()
      .map(_.sortBy(_.code.toLowerCase))

  def getIncidentIdentifications()(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    referenceDataConnector
      .getIncidentIdentifications()
      .map(_.sortBy(_.qualifier.toLowerCase))

  def getTransportIdentifications()(implicit hc: HeaderCarrier): Future[Seq[Identification]] =
    referenceDataConnector
      .getTransportIdentifications()
      .map(_.sortBy(_.`type`.toLowerCase))

  // TODO - filter based on location type
  // Need 2 separate methods - one for location of goods and one for incident location
  // The location of goods one needs to be filtered by TypeOfLocationPage
  def getIdentifications()(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    referenceDataConnector
      .getIdentifications()
      .map(_.sortBy(_.qualifier.toLowerCase))

  def getIdentifications(locationType: TypeOfLocation)(implicit hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] =
    getIdentifications()

  // TODO - filter based on IsSimplifiedProcedurePage (see TypeOfLocation.scala)
  def getTypesOfLocation()(implicit hc: HeaderCarrier): Future[Seq[TypeOfLocation]] =
    referenceDataConnector
      .getTypesOfLocation()
      .map(_.sortBy(_.`type`.toLowerCase))
}
