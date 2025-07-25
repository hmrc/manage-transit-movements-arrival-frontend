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

package models.reference

import cats.Order
import config.FrontendAppConfig
import models.Selectable
import forms.mappings.RichSeq
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class CustomsOffice(id: String, name: String, phoneNumber: Option[String], countryId: String) extends Selectable {

  override def toString: String = s"$name ($id)"

  override val value: String = id
}

object CustomsOffice {

  implicit val format: OFormat[CustomsOffice] = Json.format[CustomsOffice]

  def reads(config: FrontendAppConfig): Reads[CustomsOffice] =
    if (config.phase6Enabled) {
      (
        (__ \ "referenceNumber").read[String] and
          (__ \ "customsOfficeLsd" \ "customsOfficeUsualName").read[String] and
          (__ \ "phoneNumber").readNullable[String] and
          (__ \ "countryCode").read[String]
      )(CustomsOffice.apply)
    } else {
      Json.reads[CustomsOffice]
    }

  implicit val order: Order[CustomsOffice] = (x: CustomsOffice, y: CustomsOffice) => (x, y).compareBy(_.toString)

  def listReads(config: FrontendAppConfig): Reads[List[CustomsOffice]] =
    if (config.phase6Enabled) {
      Reads.list(reads(config))
    } else {
      case class TempCustomsOffice(customsOffice: CustomsOffice, languageCode: String)

      implicit val reads: Reads[TempCustomsOffice] = (
        __.read[CustomsOffice] and
          (__ \ "languageCode").read[String]
      )(TempCustomsOffice.apply)

      Reads {
        case JsArray(customsOffices) =>
          JsSuccess {
            customsOffices
              .flatMap(_.asOpt[TempCustomsOffice])
              .toSeq
              .groupByPreserveOrder(_.customsOffice.id)
              .flatMap {
                case (_, customsOffices) =>
                  customsOffices
                    .find(_.languageCode == "EN")
                    .orElse(customsOffices.headOption)
              }
              .map(_.customsOffice)
              .toList
          }
        case _ => JsError("Expected customs offices to be in a JsArray")
      }
    }

  def queryParameters(
    roles: Seq[String] = Nil,
    countryCodes: Seq[String] = Nil
  )(config: FrontendAppConfig): Seq[(String, String)] =
    if (config.phase6Enabled) {
      Seq(
        countryCodes.map("countryCodes" -> _),
        roles.map("roles" -> _)
      ).flatten
    } else {
      Seq(
        countryCodes.map("data.countryId" -> _),
        roles.map("data.roles.role" -> _)
      ).flatten
    }

}
