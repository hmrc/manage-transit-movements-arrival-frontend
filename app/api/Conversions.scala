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
import models.journeyDomain.incident._
import models.journeyDomain.incident.equipment.EquipmentsDomain
import models.{Index, UserAnswers}
import pages.incident.ContainerIndicatorYesNoPage

object Conversions {

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
