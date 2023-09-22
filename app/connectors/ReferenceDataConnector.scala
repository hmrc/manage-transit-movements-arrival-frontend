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

package connectors

import config.FrontendAppConfig
import logging.Logging
import models.reference._
import play.api.http.Status._
import play.api.libs.json.Reads
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  def getCustomsOfficesForCountry(countryCode: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CustomsOffice]] = {
    val url = s"${config.customsReferenceDataUrl}/filtered-lists/CustomsOffices"
    val queryParams: Seq[(String, String)] = Seq(
      "data.countryId"  -> countryCode.code,
      "data.roles.role" -> "DES"
    )
    http.GET[Seq[CustomsOffice]](url = url, headers = version2Header, queryParams = queryParams)
  }

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A]): HttpReads[Seq[A]] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          (response.json \ "data").validate[Seq[A]].getOrElse {
            throw new IllegalStateException("[ReferenceDataConnector][responseHandlerGeneric] Reference data could not be parsed")
          }
        case NO_CONTENT =>
          Nil
        case NOT_FOUND =>
          logger.warn("[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned NOT_FOUND")
          throw new IllegalStateException("[ReferenceDataConnector][responseHandlerGeneric] Reference data could not be found")
        case other =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Invalid downstream status $other")
          throw new IllegalStateException(s"[ReferenceDataConnector][responseHandlerGeneric] Invalid downstream Status $other")
      }
    }

  private def version2Header = Seq(
    "Accept" -> "application/vnd.hmrc.2.0+json"
  )

  def getCountries(listName: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/$listName"
    http.GET[Seq[Country]](url = url, headers = version2Header)
  }

  def getNationalities()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Nationality]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/Nationality"
    http.GET[Seq[Nationality]](url = url, headers = version2Header)
  }

  def getUnLocodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[UnLocode]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/UnLocodeExtended"
    http.GET[Seq[UnLocode]](url = url, headers = version2Header)
  }

  def getIncidentCodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[IncidentCode]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/IncidentCode"
    http.GET[Seq[IncidentCode]](url = url, headers = version2Header)
  }

  def getIncidentIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/QualifierOfIdentificationIncident"
    http.GET[Seq[QualifierOfIdentification]](url = url, headers = version2Header)
  }

  // TODO - unit tests
  def getIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[QualifierOfIdentification]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/QualifierOfTheIdentification"
    http.GET[Seq[QualifierOfIdentification]](url = url, headers = version2Header)
  }

  // TODO - unit tests
  def getTypesOfLocation()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[TypeOfLocation]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/TypeOfLocation"
    http.GET[Seq[TypeOfLocation]](url = url, headers = version2Header)
  }

  // TODO - unit tests
  def getTransportIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Identification]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/TypeOfIdentificationOfMeansOfTransport"
    http.GET[Seq[Identification]](url = url, headers = version2Header)
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CountryCode]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/CountryWithoutZip"
    http.GET[Seq[CountryCode]](url = url, headers = version2Header)
  }
}
