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

package api

import generated._
import models.identification.ProcedureType
import models.journeyDomain.{ArrivalDomain, ArrivalPostTransitionDomain}
import models.journeyDomain.identification.{AuthorisationsDomain, IdentificationDomain}
import models.journeyDomain.incident.{IncidentAddressLocationDomain, IncidentCoordinatesLocationDomain, IncidentUnLocodeLocationDomain, IncidentsDomain}
import models.journeyDomain.locationOfGoods._
import models.reference.CustomsOffice
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

import scala.xml.NamespaceBinding

object Conversions {

  val scope: NamespaceBinding              = scalaxb.toScope(Some("ncts") -> "http://ncts.dgtaxud.ec")
  val formatterNoMillis: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss")

  def message: MESSAGE_FROM_TRADERSequence =
    MESSAGE_FROM_TRADERSequence(
      None,
      MESSAGE_1Sequence(
        "NCTS",
        ApiXmlHelpers.toDate(DateTime.now().toString(formatterNoMillis)),
        "CC007C" // TODO - check this with API team? What should this be set to?
      )
    )

  def messageType: MessageType007 = MessageType007.fromString("CC007C", scope)

  // TODO - What should this be?
  def correlationIdentifier = CORRELATION_IDENTIFIERSequence(None)

  def transitOperation(domain: IdentificationDomain, incidents: Boolean): TransitOperationType02 =
    TransitOperationType02(
      domain.mrn.toString,
      arrivalNotificationDateAndTime = ApiXmlHelpers.toDate(DateTime.now().toString()),
      simplifiedProcedure = ApiXmlHelpers.boolToFlag(domain.procedureType match {
        case ProcedureType.Simplified => true
        case _                        => false
      }),
      incidentFlag = ApiXmlHelpers.boolToFlag(incidents)
    )

  def authorisations(domain: Option[AuthorisationsDomain]): Seq[AuthorisationType01] =
    domain
      .map(
        authorisation =>
          authorisation.authorisations.map(
            a =>
              AuthorisationType01(
                authorisation.authorisations.indexOf(a).toString,
                a.`type`.toString,
                a.referenceNumber
              )
          )
      )
      .getOrElse(Seq.empty)

  def customsOfficeOfDestination(customsOffice: CustomsOffice): CustomsOfficeOfDestinationActualType03 =
    CustomsOfficeOfDestinationActualType03(customsOffice.id)

  def traderAtDestination(identificationDomain: IdentificationDomain): TraderAtDestinationType01 =
    TraderAtDestinationType01(identificationDomain.identificationNumber)

  def consignment(domain: ArrivalPostTransitionDomain): ConsignmentType01 =
    ConsignmentType01(
      LocationOfGoodsType01(
        domain.locationOfGoods.typeOfLocation.code,
        domain.locationOfGoods.qualifierOfIdentificationDetails.qualifierOfIdentification,
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case AuthorisationNumberDomain(authorisationNumber, _, _) => Some(authorisationNumber)
          case _                                                    => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case AuthorisationNumberDomain(_, additionalIdentifier, _) => additionalIdentifier
          case _                                                     => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case UnlocodeDomain(code, _) => Some(code.unLocodeExtendedCode)
          case _                       => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case CustomsOfficeDomain(customsOffice) => Some(CustomsOfficeType01(customsOffice.id))
          case _                                  => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case CoordinatesDomain(coordinates, _) => Some(GNSSType(coordinates.latitude, coordinates.longitude))
          case _                                 => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case EoriNumberDomain(eoriNumber, _, _) => Some(EconomicOperatorType03(eoriNumber))
          case _                                  => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case AddressDomain(country, address, _) =>
            Some(AddressType14(address.numberAndStreet, address.postalCode, address.city, country.code.code))
          case _ => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails match {
          case PostalCodeDomain(address, _) =>
            Some(PostcodeAddressType02(Some(address.streetNumber), address.postalCode, address.country.code.code))
          case _ => None
        },
        domain.locationOfGoods.qualifierOfIdentificationDetails.contactPerson.map(
          p => ContactPersonType06(p.name, p.phoneNumber)
        )
      ),
      incidentsSection(domain.incidents)
    )

  private def incidentsSection(domain: Option[IncidentsDomain]): Seq[IncidentType01] =
    domain
      .map(
        incidentsDomain =>
          incidentsDomain.incidents.map(
            incident =>
              IncidentType01(
                sequenceNumber = incidentsDomain.incidents.indexOf(incident).toString,
                code = incident.incidentCode.code,
                text = incident.incidentText,
                Endorsement = incident.endorsement.map(
                  e => EndorsementType01(ApiXmlHelpers.toDate(e.date.toString), e.authority, e.location, e.country.code.code)
                ),
                Location = incident.location match {
                  case IncidentCoordinatesLocationDomain(coordinates) =>
                    LocationType01(incident.location.code, None, "GB", Some(GNSSType.apply(coordinates.latitude, coordinates.longitude)))
                  case IncidentUnLocodeLocationDomain(unLocode) =>
                    LocationType01(incident.location.code, Some(unLocode.unLocodeExtendedCode), "GB", None)
                  case IncidentAddressLocationDomain(address) =>
                    LocationType01(incident.location.code, None, "GB", None, Some(AddressType01(address.numberAndStreet, address.postalCode, address.city)))
                },
                TransportEquipment = ???, // TODO - handle equipment loops
                Transhipment = None
              )
          )
      )
      .getOrElse(Seq.empty)
}
