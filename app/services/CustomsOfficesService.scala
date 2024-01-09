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
import models.reference.{CountryCode, CustomsOffice}
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomsOfficesService @Inject() (
  referenceDataConnector: ReferenceDataConnector
)(implicit ec: ExecutionContext) {

  def getCustomsOfficesOfArrival(implicit hc: HeaderCarrier): Future[SelectableList[CustomsOffice]] = {

    def getCustomsOfficesForCountry(countryCode: String): Future[Seq[CustomsOffice]] =
      referenceDataConnector.getCustomsOfficesForCountry(CountryCode(countryCode)).map(_.toList)

    for {
      gbOffices <- getCustomsOfficesForCountry("GB")
      niOffices <- getCustomsOfficesForCountry("XI")
    } yield sort(gbOffices ++ niOffices)
  }

  private def sort(customsOffices: Seq[CustomsOffice]): SelectableList[CustomsOffice] =
    SelectableList(customsOffices.sortBy(_.name.map(_.toLowerCase)))
}
