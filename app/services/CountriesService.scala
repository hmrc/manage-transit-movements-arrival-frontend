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
import models.SelectableList
import models.reference.{Country, CountryCode}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountriesService @Inject() (referenceDataConnector: ReferenceDataConnector)(implicit ec: ExecutionContext) {

  def getCountries()(implicit hc: HeaderCarrier): Future[SelectableList[Country]] =
    getCountries("CountryCodesFullList")

  def getTransitCountries()(implicit hc: HeaderCarrier): Future[SelectableList[Country]] =
    getCountries("CountryCodesCommonTransit")

  private def getCountries(listName: String)(implicit hc: HeaderCarrier): Future[SelectableList[Country]] =
    referenceDataConnector
      .getCountries(listName)
      .map(_.resolve())
      .map(SelectableList(_))

  def doesCountryRequireZip(country: Country)(implicit hc: HeaderCarrier): Future[Boolean] =
    referenceDataConnector
      .getCountryWithoutZip(country.code)
      .map(_.isNotDefined)

  def getCountriesWithoutZip()(implicit hc: HeaderCarrier): Future[Seq[CountryCode]] =
    referenceDataConnector
      .getCountriesWithoutZip()
      .map(_.resolve())
      .map(_.toSeq)
}
