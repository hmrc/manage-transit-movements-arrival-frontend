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
import play.api.libs.json.{__, Reads}

object Consignment {

  def transform(uA: UserAnswers): ConsignmentType01 =
    uA.data.as[ConsignmentType01](consignmentType01.reads)

}

object consignmentType01 {

  implicit val reads: Reads[ConsignmentType01] = (
    (__ \ "locationOfGoods").read[LocationOfGoodsType01](locationOfGoodsType01.reads) and
      (Seq.empty: Reads[Seq[IncidentType01]])
  ).apply {
    (locationOfGoods, incident) =>
      ConsignmentType01(
        LocationOfGoods = locationOfGoods,
        Incident = incident
      )
  }

}

object locationOfGoodsType01 {

  private lazy val convertQualifierOfIdentification: String => String = {
    case "postalCode"              => "T"
    case "unlocode"                => "U"
    case "customsOfficeIdentifier" => "V"
    case "coordinates"             => "W"
    case "eoriNumber"              => "X"
    case "authorisationNumber"     => "Y"
    case "address"                 => "Z"
    case _                         => throw new Exception("Invalid qualifier of identification value")
  }

  private lazy val convertTypeOfLocation: String => String = {
    case "designatedLocation" => "A"
    case "authorisedPlace"    => "B"
    case "approvedPlace"      => "C"
    case "other"              => "D"
    case _                    => throw new Exception("Invalid type of location value")
  }

  implicit val reads: Reads[LocationOfGoodsType01] = (
    (__ \ "typeOfLocation").read[String].map(convertTypeOfLocation) and
      (__ \ "qualifierOfIdentification").read[String].map(convertQualifierOfIdentification) and
      (__ \ "qualifierOfIdentificationDetails" \ "authorisationNumber").readNullable[String] and
      (__ \ "qualifierOfIdentificationDetails" \ "additionalIdentifier").readNullable[String] and
      (__ \ "qualifierOfIdentificationDetails" \ "unLocode").readNullable[String] and
      (__ \ "qualifierOfIdentificationDetails" \ "customsOffice").readNullable[CustomsOfficeType01](customsOfficeType01.reads) and
      (__ \ "qualifierOfIdentificationDetails" \ "coordinates").readNullable[GNSSType](gnssType.reads) and
      (__ \ "qualifierOfIdentificationDetails" \ "eori").readNullable[EconomicOperatorType03](economicOperatorType03.reads) and
      (__ \ "qualifierOfIdentificationDetails").read[Option[AddressType14]](addressType14.reads) and
      (__ \ "qualifierOfIdentificationDetails").read[Option[PostcodeAddressType02]](postcodeAddressType02.reads) and
      (__ \ "contactPerson").readNullable[ContactPersonType06](contactPersonType06.reads)
  )(LocationOfGoodsType01.apply _)

}

object economicOperatorType03 {

  implicit val reads: Reads[EconomicOperatorType03] =
    __.read[String].map(EconomicOperatorType03)
}

object customsOfficeType01 {

  implicit val reads: Reads[CustomsOfficeType01] =
    (__ \ "id").read[String].map(CustomsOfficeType01)
}

object gnssType {

  implicit val reads: Reads[GNSSType] = (
    (__ \ "latitude").read[String] and
      (__ \ "longitude").read[String]
  )(GNSSType.apply _)
}

object addressType14 {

  implicit val reads: Reads[Option[AddressType14]] = (
    (__ \ "address" \ "numberAndStreet").readNullable[String] and
      (__ \ "address" \ "postalCode").readNullable[String] and
      (__ \ "address" \ "city").readNullable[String] and
      (__ \ "country" \ "code").readNullable[String]
  ).tupled.map {
    case (Some(streetAndNumber), postcode, Some(city), Some(country)) =>
      Some(AddressType14(streetAndNumber, postcode, city, country))
    case _ => None
  }

}

object postcodeAddressType02 {

  implicit val reads: Reads[Option[PostcodeAddressType02]] = (
    (__ \ "address" \ "numberAndStreet").readNullable[String] and
      (__ \ "address" \ "postalCode").readNullable[String] and
      (__ \ "country" \ "code").readNullable[String]
  ).tupled.map {
    case (streetAndNumber, Some(postcode), Some(country)) =>
      Some(PostcodeAddressType02(streetAndNumber, postcode, country))
    case _ => None
  }

}

object contactPerson {

  val name: String            = "name"
  val telephoneNumber: String = "telephoneNumber"

  def reads[T](apply: (String, String, Option[String]) => T): Reads[T] = (
    (__ \ name).read[String] and
      (__ \ telephoneNumber).read[String] and
      None
  )(apply)

}

object contactPersonType06 {

  implicit val reads: Reads[ContactPersonType06] =
    contactPerson.reads(ContactPersonType06)

}
