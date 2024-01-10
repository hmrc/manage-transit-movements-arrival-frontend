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
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClientV2) extends Logging {

  private def headers: Seq[(String, String)] = Seq(
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
    val url = url"${config.customsReferenceDataUrl}/filtered-lists/CustomsOffices?data.countryId=${countryCode.code}&data.roles.role=DES"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[CustomsOffice]]
  }

  def getCountries(listName: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[Country]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/$listName"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[Country]]
  }

  def getNationalities()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[Nationality]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/Nationality"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[Nationality]]
  }

  def getUnLocodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[UnLocode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/UnLocodeExtended"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[UnLocode]]
  }

  def getUnLocode(unLocode: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UnLocode] = {
    val url = url"${config.customsReferenceDataUrl}/filtered-lists/UnLocodeExtended?data.unLocodeExtendedCode=$unLocode"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[UnLocode]]
      .map(_.head)
  }

  def getIncidentCodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[IncidentCode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/IncidentCode"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[IncidentCode]]
  }

  def getIncidentIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[QualifierOfIdentification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/QualifierOfIdentificationIncident"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[QualifierOfIdentification]]
  }

  def getIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[QualifierOfIdentification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/QualifierOfTheIdentification"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[QualifierOfIdentification]]
  }

  def getTypesOfLocation()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[TypeOfLocation]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/TypeOfLocation"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[TypeOfLocation]]
  }

  def getTransportIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[Identification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/TypeOfIdentificationOfMeansOfTransport"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[Identification]]
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptyList[CountryCode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/CountryWithoutZip"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[CountryCode]]
  }

  def getCountryWithoutZip(country: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryCode] = {
    val url = url"${config.customsReferenceDataUrl}/filtered-lists/CountryWithoutZip?data.code=${country.code}"
    http
      .get(url)
      .setHeader(headers: _*)
      .execute[NonEmptyList[CountryCode]]
      .map(_.head)
  }
}

object ReferenceDataConnector {

  class NoReferenceDataFoundException extends Exception("The reference data call was successful but the response body is empty.")
}
