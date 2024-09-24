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

import cats.Order
import cats.data.NonEmptySet
import config.FrontendAppConfig
import connectors.ReferenceDataConnector.NoReferenceDataFoundException
import models.reference._
import play.api.Logging
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

  implicit def responseHandlerGeneric[A](implicit reads: Reads[List[A]], order: Order[A]): HttpReads[NonEmptySet[A]] =
    (_: String, url: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          (response.json \ "data").validate[List[A]] match {
            case JsSuccess(Nil, _) =>
              throw new NoReferenceDataFoundException(url)
            case JsSuccess(head :: tail, _) =>
              NonEmptySet.of(head, tail*)
            case JsError(errors) =>
              throw JsResultException(errors)
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          throw new Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}")
      }

  def getCustomsOfficesForCountry(countryCodes: String*)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[CustomsOffice]] = {
    val query = countryCodes.map("data.countryId" -> _) :+ ("data.roles.role" -> "DES")
    val url   = url"${config.customsReferenceDataUrl}/lists/CustomsOffices"
    http
      .get(url)
      .transform(_.withQueryStringParameters(query*))
      .setHeader(headers*)
      .execute[NonEmptySet[CustomsOffice]]
  }

  def getCountries(listName: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[Country]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/$listName"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[Country]]
  }

  def getNationalities()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[Nationality]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/Nationality"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[Nationality]]
  }

  def getUnLocodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[UnLocode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/UnLocodeExtended"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[UnLocode]]
  }

  def getUnLocode(unLocode: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[UnLocode] = {
    val url = url"${config.customsReferenceDataUrl}/lists/UnLocodeExtended"
    http
      .get(url)
      .transform(_.withQueryStringParameters("data.unLocodeExtendedCode" -> unLocode))
      .setHeader(headers*)
      .execute[NonEmptySet[UnLocode]]
      .map(_.head)
  }

  def getIncidentCodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[IncidentCode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/IncidentCode"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[IncidentCode]]
  }

  def getIncidentIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[QualifierOfIdentification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/QualifierOfIdentificationIncident"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[QualifierOfIdentification]]
  }

  def getIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[QualifierOfIdentification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/QualifierOfTheIdentification"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[QualifierOfIdentification]]
  }

  def getTypesOfLocation()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[TypeOfLocation]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/TypeOfLocation"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[TypeOfLocation]]
  }

  def getTransportIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[Identification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/TypeOfIdentificationOfMeansOfTransport"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[Identification]]
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[NonEmptySet[CountryCode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/CountryWithoutZip"
    http
      .get(url)
      .setHeader(headers*)
      .execute[NonEmptySet[CountryCode]]
  }

  def getCountryWithoutZip(country: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[CountryCode] = {
    val url = url"${config.customsReferenceDataUrl}/lists/CountryWithoutZip"
    http
      .get(url)
      .transform(_.withQueryStringParameters("data.code" -> country.code))
      .setHeader(headers*)
      .execute[NonEmptySet[CountryCode]]
      .map(_.head)
  }
}

object ReferenceDataConnector {

  class NoReferenceDataFoundException(url: String) extends Exception(s"The reference data call was successful but the response body is empty: $url")
}
