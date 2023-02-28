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
      (__ \ "incidents").readArray[IncidentType01](incidentType01.reads)
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

object addressType01 {

  implicit val reads: Reads[Option[AddressType01]] = (
    (__ \ "address" \ "numberAndStreet").readNullable[String] and
      (__ \ "address" \ "postalCode").readNullable[String] and
      (__ \ "address" \ "city").readNullable[String]
  ).tupled.map {
    case (Some(streetAndNumber), postcode, Some(city)) =>
      Some(AddressType01(streetAndNumber, postcode, city))
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

object incidentType01 {

  private lazy val convertIncidentCode: String => String = {
    case "deviatedFromItinerary"         => "1"
    case "sealsBrokenOrTampered"         => "2"
    case "transferredToAnotherTransport" => "3"
    case "partiallyOrFullyUnloaded"      => "4"
    case "carrierUnableToComply"         => "5"
    case "unexpectedlyChanged"           => "6"
    case _                               => throw new Exception("Invalid incident code")
  }

  def reads(index: Int): Reads[IncidentType01] = (
    (index.toString: Reads[String]) and
      (__ \ "incidentCode").read[String].map(convertIncidentCode) and
      (__ \ "incidentText").read[String] and
      (__ \ "endorsement").readNullable[EndorsementType01](endorsementType01.reads) and
      __.read[LocationType01](locationType01.reads) and
      (__ \ "equipments").readArray[TransportEquipmentType01](transportEquipmentType01.reads) and
      __.read[Option[TranshipmentType01]](transhipmentType01.reads)
  ).apply {
    (a, b, c, d, e, f, g) =>
      IncidentType01(
        sequenceNumber = a,
        code = b,
        text = c,
        Endorsement = d,
        Location = e,
        TransportEquipment = f,
        Transhipment = g
      )
  }

}

object endorsementType01 {

  def reads: Reads[EndorsementType01] = (
    (__ \ "date").read[String] and
      (__ \ "authority").read[String] and
      (__ \ "location").read[String] and
      (__ \ "country" \ "code").read[String]
  ).apply {
    (a, b, c, d) =>
      EndorsementType01(
        date = a,
        authority = b,
        place = c,
        country = d
      )
  }

}

object locationType01 {

  private lazy val convertQualifierOfIdentification: String => String = {
    case "unlocode"    => "U"
    case "coordinates" => "W"
    case "address"     => "Z"
    case _             => throw new Exception("Invalid qualifier of identification value")
  }

  def reads: Reads[LocationType01] = (
    (__ \ "qualifierOfIdentification").read[String].map(convertQualifierOfIdentification) and
      (__ \ "unLocode" \ "unLocodeExtendedCode").readNullable[String] and
      (__ \ "incidentCountry" \ "code").read[String] and
      (__ \ "coordinates").readNullable[GNSSType](gnssType.reads) and
      __.read[Option[AddressType01]](addressType01.reads)
  ).apply {
    (a, b, c, d, e) =>
      LocationType01(
        qualifierOfIdentification = a,
        UNLocode = b,
        country = c,
        GNSS = d,
        Address = e
      )
  }

}

object transportEquipmentType01 {

  def apply(
    sequenceNumber: String,
    containerIdentificationNumber: Option[String],
    Seal: Seq[SealType05],
    GoodsReference: Seq[GoodsReferenceType01]
  ): TransportEquipmentType01 =
    TransportEquipmentType01(sequenceNumber, containerIdentificationNumber, Some(Seal.length), Seal, GoodsReference)

  def reads(index: Int): Reads[TransportEquipmentType01] = (
    (index.toString: Reads[String]) and
      (__ \ "containerIdentificationNumber").readNullable[String] and
      (__ \ "seals").readArray[SealType05](sealType05.reads) and
      (__ \ "itemNumbers").readArray[GoodsReferenceType01](goodsReferenceType01.reads)
  )(transportEquipmentType01.apply _)

}

object sealType05 {

  def reads(index: Int): Reads[SealType05] = (
    (index.toString: Reads[String]) and
      (__ \ "sealIdentificationNumber").read[String]
  )(SealType05.apply _)

}

object goodsReferenceType01 {

  def reads(index: Int): Reads[GoodsReferenceType01] = (
    (index.toString: Reads[String]) and
      (__ \ "itemNumber").read[String].map(BigInt(_))
  )(GoodsReferenceType01.apply _)

}

object transhipmentType01 {

  def reads: Reads[Option[TranshipmentType01]] = (
    (__ \ "containerIndicatorYesNo").readWithDefault[Boolean](false) and
      (__ \ "transportMeans").readNullable[TransportMeansType01](transportMeansType01.reads)
  ).apply {
    (containerIndicator, transportMeans) =>
      transportMeans.map(
        x =>
          TranshipmentType01(
            containerIndicator = containerIndicator,
            TransportMeans = x
          )
      )
  }

}

object transportMeansType01 {

  lazy val convertTypeOfIdentification: String => String = {
    case "imoShipIdNumber"        => "10"
    case "seaGoingVessel"         => "11"
    case "wagonNumber"            => "20"
    case "trainNumber"            => "21"
    case "regNumberRoadVehicle"   => "30"
    case "regNumberRoadTrailer"   => "31"
    case "iataFlightNumber"       => "40"
    case "regNumberAircraft"      => "41"
    case "europeanVesselIdNumber" => "80"
    case "inlandWaterwaysVehicle" => "81"
    case "unknown"                => "99"
    case _                        => throw new Exception("Invalid type of identification value")
  }

  def reads: Reads[TransportMeansType01] = (
    (__ \ "identification").read[String].map(convertTypeOfIdentification) and
      (__ \ "identificationNumber").read[String] and
      (__ \ "transportNationality" \ "code").read[String]
  )(TransportMeansType01.apply _)

}
