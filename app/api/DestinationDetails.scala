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

package api

import generated.{CustomsOfficeOfDestinationActualType03, TraderAtDestinationType01}
import models.UserAnswers
import play.api.libs.json.{__, Reads}

object DestinationDetails {

  def customsOfficeOfDestination(uA: UserAnswers): CustomsOfficeOfDestinationActualType03 =
    uA.data.as[CustomsOfficeOfDestinationActualType03](customsOfficeOfDestinationActualType03.reads)

  def traderAtDestination(uA: UserAnswers): TraderAtDestinationType01 =
    uA.data.as[TraderAtDestinationType01](traderAtDestinationType01.reads)

}

object customsOfficeOfDestinationActualType03 {

  def reads: Reads[CustomsOfficeOfDestinationActualType03] =
    (identificationPath \ "destinationOffice")
      .read[String]
      .map(
        x => CustomsOfficeOfDestinationActualType03(x)
      )

}

object traderAtDestinationType01 {

  def reads: Reads[TraderAtDestinationType01] =
    (identificationPath \ "identificationNumber")
      .read[String]
      .map(
        x => TraderAtDestinationType01(x)
      )

}
