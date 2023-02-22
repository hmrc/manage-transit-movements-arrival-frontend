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

import generated._
import models.UserAnswers
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}

object Consignment {

  def transform(uA: UserAnswers): ConsignmentType01 =
    uA.data.as[ConsignmentType01](consignmentType01.reads)
}

object consignmentType01 {

  implicit val reads: Reads[ConsignmentType01] = (
      (__ \ "???").read[LocationOfGoodsType01](locationOfGoodsType01.reads) and
      (__ \ "???").readArray[IncidentType01](incidentType01.reads)
    ).apply {
    (x, y) =>
      ConsignmentType01(
        LocationOfGoods = x,
        Incident = y
      )
  }

}

object locationOfGoodsType01 {

  implicit val reads: Reads[LocationOfGoodsType01] = (
    (__ \ "???").read[String] and
      (__ \ "???").read[String] and
      (__ \ "???").readNullable[String] and
      (__ \ "???").readNullable[String] and
      (__ \ "???").readNullable[String] and
      (__ \ "???").readNullable[CustomsOfficeType01](customsOfficeType01.reads) and
      (__ \ "???").readNullable[GNSSType](gNSSType.reads)
    ).apply {
    (a, b, c, d, e, f, g, h, i, j, k) =>
      LocationOfGoodsType01(
        typeOfLocation = a,
        qualifierOfIdentification = b,
        authorisationNumber = c,
        additionalIdentifier = d,
        UNLocode = e,
        CustomsOffice = f,
        GNSS = g,
        EconomicOperator = h,
        Address = i,
        PostcodeAddress = j,
        ContactPerson = k
      )
  }

}

object customsOfficeType01 {

  def reads: Reads[CustomsOfficeType01] =
    (__ \ "???")
      .read[String]
      .map(
        x => CustomsOfficeType01(x)
      )

}

object gNSSType {

  implicit val reads: Reads[GNSSType] = (
    (__ \ "???").read[String] and
      (__ \ "???").read[String]
    ).apply {
    (x, y) =>
      GNSSType(x, y)
  }
}

object incidentType01 {

  implicit def reads(index: Int): Reads[IncidentType01] = (
    ???
    ).apply {
    (a, b, c ,d, e, f) =>
      IncidentType01(
        sequenceNumber = index.toString,
        code = a,
        text = b,
        Endorsement = c,
        Location = d,
        TransportEquipment = e,
        Transhipment = f
      )
  }

}
