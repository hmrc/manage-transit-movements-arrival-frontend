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

import generated.TransitOperationType02
import models.UserAnswers
import play.api.libs.functional.syntax._
import play.api.libs.json.{__, Reads}
import utils.Format.formatterNoMillis

import java.time.LocalDateTime

object TransitOperation {

  def transform(uA: UserAnswers): TransitOperationType02 =
    uA.data.as[TransitOperationType02](transitOperationType02.reads(uA.mrn.toString))
}

object transitOperationType02 {

  private lazy val convertIsSimplifiedProcedure: String => Boolean = {
    case "simplified" => true
    case _            => false
  }

  def reads(mrn: String): Reads[TransitOperationType02] = (
    (identificationPath \ "isSimplifiedProcedure").read[String].map(convertIsSimplifiedProcedure) and
      (__ \ "incidentFlag").readWithDefault[Boolean](false)
  ).apply {
    (isSimplified, isIncident) =>
      TransitOperationType02(
        MRN = mrn,
        arrivalNotificationDateAndTime = LocalDateTime.now().format(formatterNoMillis),
        simplifiedProcedure = isSimplified,
        incidentFlag = isIncident
      )
  }
}
