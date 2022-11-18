/*
 * Copyright 2022 HM Revenue & Customs
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
import metrics.{MetricsService, Monitors}
import models.reference.{Country, CountryCode, CustomsOffice, UnLocode}
import uk.gov.hmrc.http.HttpReads.Implicits.readFromJson
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ReferenceDataConnector @Inject() (config: FrontendAppConfig, http: HttpClient, metricsService: MetricsService) {

  def getCustomsOffices()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CustomsOffice]] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices"

    metricsService.timeAsyncCall(Monitors.getCustomsOfficesMonitor) {
      http.GET[Seq[CustomsOffice]](serviceUrl)
    }
  }

  def getCustomsOfficesOfDepartureForCountry(countryCode: CountryCode)(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CustomsOffice]] = {
    val serviceUrl = s"${config.referenceDataUrl}/customs-offices/${countryCode.code}?role=DEP"

    metricsService.timeAsyncCall(Monitors.getCustomsOfficesOfDepartureForCountryMonitor) {
      http.GET[Seq[CustomsOffice]](serviceUrl, headers = version2Header)
    }
  }

  def getCountries(queryParameters: Seq[(String, String)])(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.referenceDataUrl}/countries"

    metricsService.timeAsyncCall(Monitors.getCountryListMonitor) {
      http.GET[Seq[Country]](serviceUrl, queryParameters)
    }
  }

  def getUnLocodes()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[UnLocode]] = {
    val serviceUrl = s"${config.referenceDataUrl}/un-locodes"
    http.GET[Seq[UnLocode]](serviceUrl, headers = version2Header)
  }

  def getAddressPostcodeBasedCountries()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[Country]] = {
    val serviceUrl = s"${config.referenceDataUrl}/country-address-postcode-based"
    http.GET[Seq[Country]](serviceUrl, headers = version2Header)
  }

  private def version2Header = Seq(
    "Accept" -> "application/vnd.hmrc.2.0+json"
  )

  def getCountriesWithoutZip()(implicit ec: ExecutionContext, hc: HeaderCarrier): Future[Seq[CountryCode]] = {
    val serviceUrl = s"${config.referenceDataUrl}/country-without-zip"
    http.GET[Seq[CountryCode]](serviceUrl, headers = version2Header)
  }

}
