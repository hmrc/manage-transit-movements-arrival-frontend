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
import models.journeyDomain.ArrivalPostTransitionDomain
import models.journeyDomain.incident._
import models.journeyDomain.incident.equipment.EquipmentsDomain
import models.journeyDomain.locationOfGoods._
import models.{Index, UserAnswers}
import pages.incident.ContainerIndicatorYesNoPage

object Conversions {

  def consignment(domain: ArrivalPostTransitionDomain, userAnswers: UserAnswers): ConsignmentType01 =
    ConsignmentType01(
      LocationOfGoods = LocationOfGoodsType01(
        typeOfLocation = domain.locationOfGoods.typeOfLocation.code,
        qualifierOfIdentification = domain.locationOfGoods.qualifierOfIdentificationDetails.qualifierOfIdentification,
        authorisationNumber = authorisationNumber(domain),
        additionalIdentifier = additionalIdentifier(domain),
        UNLocode = unLocodeExtendedCode(domain),
        CustomsOffice = customsOffice(domain),
        GNSS = coordinatesGNSSType(domain),
        EconomicOperator = economicOperator(domain),
        Address = addressNoPostcode(domain),
        PostcodeAddress = addressWithPostcode(domain),
        ContactPerson = domain.locationOfGoods.qualifierOfIdentificationDetails.contactPerson.map(
          p => ContactPersonType06(p.name, p.phoneNumber)
        )
      ),
      Incident = incidentsSection(domain.incidents, userAnswers)
    )

  private def authorisationNumber(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case AuthorisationNumberDomain(authorisationNumber, _, _) => Some(authorisationNumber)
      case _                                                    => None
    }

  private def additionalIdentifier(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case AuthorisationNumberDomain(_, additionalIdentifier, _) => additionalIdentifier
      case _                                                     => None
    }

  private def unLocodeExtendedCode(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case UnlocodeDomain(code, _) => Some(code.unLocodeExtendedCode)
      case _                       => None
    }

  private def customsOffice(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case CustomsOfficeDomain(customsOffice) => Some(CustomsOfficeType01(customsOffice.id))
      case _                                  => None
    }

  private def coordinatesGNSSType(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case CoordinatesDomain(coordinates, _) => Some(GNSSType(coordinates.latitude, coordinates.longitude))
      case _                                 => None
    }

  private def economicOperator(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case EoriNumberDomain(eoriNumber, _, _) => Some(EconomicOperatorType03(eoriNumber))
      case _                                  => None
    }

  private def addressNoPostcode(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case AddressDomain(country, address, _) =>
        Some(AddressType14(address.numberAndStreet, address.postalCode, address.city, country.code.code))
      case _ => None
    }

  private def addressWithPostcode(domain: ArrivalPostTransitionDomain) =
    domain.locationOfGoods.qualifierOfIdentificationDetails match {
      case PostalCodeDomain(address, _) =>
        Some(PostcodeAddressType02(Some(address.streetNumber), address.postalCode, address.country.code.code))
      case _ => None
    }

  private def incidentsSection(domain: Option[IncidentsDomain], userAnswers: UserAnswers): Seq[IncidentType01] =
    domain
      .map(
        incidentsDomain =>
          incidentsDomain.incidents.map {
            incident =>
              val index: Int                  = incidentsDomain.incidents.indexOf(incident)
              val containerIndicator: Boolean = userAnswers.get(ContainerIndicatorYesNoPage(Index(index))).isDefined

              IncidentType01(
                sequenceNumber = index.toString,
                code = incident.incidentCode.code,
                text = incident.incidentText,
                Endorsement = incident.endorsement.map(
                  e => EndorsementType01(ApiXmlHelpers.toDate(e.date.toString), e.authority, e.location, e.country.code.code)
                ),
                Location = incident.location match {
                  case IncidentCoordinatesLocationDomain(coordinates) =>
                    LocationType01(incident.location.code,
                                   None,
                                   incident.incidentCountry.code.code,
                                   Some(GNSSType.apply(coordinates.latitude, coordinates.longitude))
                    )
                  case IncidentUnLocodeLocationDomain(unLocode) =>
                    LocationType01(incident.location.code, Some(unLocode.unLocodeExtendedCode), incident.incidentCountry.code.code, None)
                  case IncidentAddressLocationDomain(address) =>
                    LocationType01(incident.location.code,
                                   None,
                                   incident.incidentCountry.code.code,
                                   None,
                                   Some(AddressType01(address.numberAndStreet, address.postalCode, address.city))
                    )
                },
                TransportEquipment = transportEquipmentSection(incident.equipments),
                Transhipment = transportMeansSection(incident.transportMeans, containerIndicator)
              )
          }
      )
      .getOrElse(Seq.empty)

  private def transportEquipmentSection(domain: EquipmentsDomain) =
    domain.equipments.map(
      equipment =>
        TransportEquipmentType01(
          domain.equipments.indexOf(equipment).toString,
          equipment.containerId,
          Some(BigInt(equipment.seals.seals.size)),
          equipment.seals.seals.map(
            seal =>
              SealType05(
                equipment.seals.seals.indexOf(seal).toString,
                seal.identificationNumber
              )
          ),
          equipment.itemNumbers.itemNumbers.map(
            goodsReference =>
              GoodsReferenceType01(
                equipment.itemNumbers.itemNumbers.indexOf(goodsReference).toString,
                BigInt(goodsReference.itemNumber)
              )
          )
        )
    )

  private def transportMeansSection(domain: Option[TransportMeansDomain], containerIndicator: Boolean) =
    domain.map(
      transportMeans =>
        TranshipmentType01(
          ApiXmlHelpers.boolToFlag(containerIndicator),
          TransportMeansType01(
            transportMeans.identificationType.code,
            transportMeans.identificationNumber,
            transportMeans.nationality.code
          )
        )
    )
}
