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
import connectors.ReferenceDataConnector.*
import models.reference.*
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.{JsError, JsResultException, JsSuccess, Reads}
import sttp.model.HeaderNames
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}

import java.net.URL
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClientV2) extends Logging {

  private def get[T](url: URL)(implicit ec: ExecutionContext, hc: HeaderCarrier, reads: HttpReads[Responses[T]]): Future[Responses[T]] =
    http
      .get(url)
      .setHeader(HeaderNames.Accept -> "application/vnd.hmrc.2.0+json")
      .execute[Responses[T]]

  private def getOne[T](url: URL)(implicit ec: ExecutionContext, hc: HeaderCarrier, reads: HttpReads[Responses[T]]): Future[Response[T]] =
    get[T](url).map(_.map(_.head))

  implicit def responseHandlerGeneric[A](implicit reads: Reads[List[A]], order: Order[A]): HttpReads[Responses[A]] =
    (_: String, url: String, response: HttpResponse) =>
      response.status match {
        case OK =>
          (response.json \ "data").validate[List[A]] match {
            case JsSuccess(Nil, _) =>
              Left(NoReferenceDataFoundException(url))
            case JsSuccess(head :: tail, _) =>
              Right(NonEmptySet.of(head, tail*))
            case JsError(errors) =>
              Left(JsResultException(errors))
          }
        case e =>
          logger.warn(s"[ReferenceDataConnector][responseHandlerGeneric] Reference data call returned $e")
          Left(Exception(s"[ReferenceDataConnector][responseHandlerGeneric] $e - ${response.body}"))
      }

  def getCustomsOfficesForCountry(countryCodes: String*)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[CustomsOffice]] = {
    val query = countryCodes.map("data.countryId" -> _) :+ ("data.roles.role" -> "DES")
    val url   = url"${config.customsReferenceDataUrl}/lists/CustomsOffices?$query"
    get[CustomsOffice](url)
  }

  def getCountries(listName: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[Country]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/$listName"
    get[Country](url)
  }

  def getNationalities()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[Nationality]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/Nationality"
    get[Nationality](url)
  }

  def getUnLocodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[UnLocode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/UnLocodeExtended"
    get[UnLocode](url)
  }

  def getUnLocode(unLocode: String)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[UnLocode]] = {
    val query = Seq("data.unLocodeExtendedCode" -> unLocode)
    val url   = url"${config.customsReferenceDataUrl}/lists/UnLocodeExtended?$query"
    getOne[UnLocode](url)
  }

  def getIdentifications()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[QualifierOfIdentification]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/QualifierOfTheIdentification"
    get[QualifierOfIdentification](url)
  }

  def getTypesOfLocation()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[TypeOfLocation]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/TypeOfLocation"
    get[TypeOfLocation](url)
  }

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Responses[CountryCode]] = {
    val url = url"${config.customsReferenceDataUrl}/lists/CountryWithoutZip"
    get[CountryCode](url)
  }

  def getCountryWithoutZip(country: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Response[CountryCode]] = {
    val query = Seq("data.code" -> country.code)
    val url   = url"${config.customsReferenceDataUrl}/lists/CountryWithoutZip?$query"
    getOne[CountryCode](url)
  }
}

object ReferenceDataConnector {
  type Responses[T] = Either[Exception, NonEmptySet[T]]
  type Response[T]  = Either[Exception, T]

  class NoReferenceDataFoundException(url: String) extends Exception(s"The reference data call was successful but the response body is empty: $url")
}
