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

import cats.data.NonEmptyList
import config.FrontendAppConfig
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import logging.Logging
import models.reference._
import play.api.http.Status._
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpReads, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient) extends Logging {

  private def version2Header: Seq[(String, String)] = Seq(
    "Accept" -> "application/vnd.hmrc.2.0+json"
  )

  implicit def responseHandlerGeneric[A](implicit reads: Reads[A]): HttpReads[NonEmptyList[A]] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case OK =>
          (response.json \ "data").validate[List[A]] match {
            case JsSuccess(Nil, _) =>
              throw new NoReferenceDataFoundException
            case JsSuccess(head :: tail, _) =>
              NonEmptyList(head, tail)
            case JsError(errors) =>
              throw JsResultException(errors)
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          throw new Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}")
      }
    }

  def getCustomsOfficesForCountry(countryCode: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[CustomsOffice]] = {
    val url = s"${config.customsReferenceDataUrl}/filtered-lists/CustomsOffices"
    val queryParams: Seq[(String, String)] = Seq(
      "data.countryId"  -> countryCode.code,
      "data.roles.role" -> "DES"
    )
    http.GET[NonEmptyList[CustomsOffice]](url = url, headers = version2Header, queryParams = queryParams)
  }

  def getCountries(listName: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[Country]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/$listName"
    http.GET[NonEmptyList[Country]](url = url, headers = version2Header)
  }

  def getNationalities()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[Nationality]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/Nationality"
    http.GET[NonEmptyList[Nationality]](url = url, headers = version2Header)
  }

  def getUnLocodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[UnLocode]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/UnLocodeExtended"
    http.GET[NonEmptyList[UnLocode]](url = url, headers = version2Header)
  }

  def getUnLocode(unLocode: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UnLocode] = {
    val url: String                        = s"${config.customsReferenceDataUrl}/filtered-lists/UnLocodeExtended"
    val queryParams: Seq[(String, String)] = Seq("data.unLocodeExtendedCode" -> unLocode)
    http.GET[NonEmptyList[UnLocode]](url, headers = version2Header, queryParams = queryParams).map(_.head)
  }

  def getIncidentCodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[IncidentCode]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/IncidentCode"
    http.GET[NonEmptyList[IncidentCode]](url = url, headers = version2Header)
  }

  def getIncidentIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[QualifierOfIdentification]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/QualifierOfIdentificationIncident"
    http.GET[NonEmptyList[QualifierOfIdentification]](url = url, headers = version2Header)
  }

  def getIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[QualifierOfIdentification]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/QualifierOfTheIdentification"
    http.GET[NonEmptyList[QualifierOfIdentification]](url = url, headers = version2Header)
  }

  def getTypesOfLocation()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[TypeOfLocation]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/TypeOfLocation"
    http.GET[NonEmptyList[TypeOfLocation]](url = url, headers = version2Header)
  }

  def getTransportIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[Identification]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/TypeOfIdentificationOfMeansOfTransport"
    http.GET[NonEmptyList[Identification]](url = url, headers = version2Header)
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[CountryCode]] = {
    val url = s"${config.customsReferenceDataUrl}/lists/CountryWithoutZip"
    http.GET[NonEmptyList[CountryCode]](url = url, headers = version2Header)
  }

  def getCountryWithoutZip(country: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryCode] = {
    val url                                = s"${config.customsReferenceDataUrl}/filtered-lists/CountryWithoutZip"
    val queryParams: Seq[(String, String)] = Seq("data.code" -> country.code)
    http.GET[NonEmptyList[CountryCode]](url = url, headers = version2Header, queryParams = queryParams).map(_.head)
  }
}

object ReferenceDataConnector {

  class NoReferenceDataFoundException extends Exception("The reference data call was successful but the response body is empty.")
}
