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
import models.{DynamicAddress, UserAnswers}
import models.identification.ProcedureType
import models.identification.authorisation.AuthorisationType
import models.journeyDomain.{EitherType, ReaderError, UserAnswersReader}
import models.journeyDomain.identification.{AuthorisationDomain, AuthorisationsDomain}
import models.reference.Country
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import pages.identification.{DestinationOfficePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import pages.incident.IncidentFlagPage
import pages.locationOfGoods._
import pages.sections.identification.AuthorisationsSection
import pages.sections.incident.{IncidentSection, IncidentsSection}
import play.api.libs.json.{JsError, JsSuccess, Json}

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

  def transitOperation(userAnswers: UserAnswers): Either[String, TransitOperationType02] =
    for {
      sp           <- userAnswers.getAsEither(IsSimplifiedProcedurePage)
      incidentFlag <- userAnswers.getAsEither(IncidentFlagPage)
    } yield TransitOperationType02(
      userAnswers.mrn.toString,
      arrivalNotificationDateAndTime = ApiXmlHelpers.toDate(DateTime.now().toString()),
      simplifiedProcedure = ApiXmlHelpers.boolToFlag(sp match {
        case ProcedureType.Simplified => true
        case _                        => false
      }),
      incidentFlag = ApiXmlHelpers.boolToFlag(incidentFlag)
    )

  implicit val jsonFormat = Json.format[AuthorisationType01]

  def authorisations(userAnswers: UserAnswers): Either[String, Seq[AuthorisationType01]] =
    for {
      authSection <- userAnswers.getOptional(AuthorisationsSection)
      result <- {
        authSection match {
          case Some(section) =>
            section.validate[Seq[AuthorisationType01]] match {
              case JsSuccess(authorisations, _) => Right(authorisations)
              case JsError(errors)              => Left(errors.toString)
            }
          case None => Right(Seq.empty)
        }
      }
    } yield result

  def customsOfficeOfDestination(userAnswers: UserAnswers): Either[String, CustomsOfficeOfDestinationActualType03] =
    for {
      customsOfficeOfDestination <- userAnswers.getAsEither(DestinationOfficePage)
    } yield CustomsOfficeOfDestinationActualType03(customsOfficeOfDestination.id)

  def traderAtDestination(userAnswers: UserAnswers): Either[String, TraderAtDestinationType01] =
    for {
      traderAtDestination <- userAnswers.getAsEither(IdentificationNumberPage)
    } yield TraderAtDestinationType01(traderAtDestination)

  def consignment(userAnswers: UserAnswers): Either[String, ConsignmentType01] =
    for {
      typeOfLocation            <- userAnswers.getAsEither(TypeOfLocationPage)
      qualifierOfIdentification <- userAnswers.getAsEither(QualifierOfIdentificationPage)
      authorisationNumber       <- userAnswers.getOptional(AuthorisationNumberPage)
      additionalIdentifier      <- userAnswers.getOptional(AdditionalIdentifierPage)
      unlocode                  <- userAnswers.getOptional(UnlocodePage)
      customsOffice             <- userAnswers.getOptional(CustomsOfficePage)
      gnss                      <- userAnswers.getOptional(CoordinatesPage)
      identificationNumber      <- userAnswers.getOptional(IdentificationNumberPage)
      address                   <- userAnswers.getOptional(AddressPage)
      country                   <- userAnswers.getOptional(CountryPage)
      contactPersonName         <- userAnswers.getOptional(ContactPersonNamePage)
      contactPersonTel          <- userAnswers.getOptional(ContactPersonTelephonePage)
    } yield ConsignmentType01(
      LocationOfGoodsType01(
        typeOfLocation.code,
        qualifierOfIdentification.code,
        authorisationNumber,
        additionalIdentifier,
        unlocode.map(
          code => code.name
        ),
        customsOffice.map(
          co => CustomsOfficeType01(co.id)
        ),
        gnss.map(
          coordinates => GNSSType(coordinates.latitude, coordinates.longitude)
        ),
        identificationNumber.map(
          ident => EconomicOperatorType03(ident)
        ),
        getAddressNoPostcode(address, country),
        getAddressWithPostcode(address, country),
        contactPersonName.map(
          name =>
            ContactPersonType06(name,
                                contactPersonTel.getOrElse(
                                  throw new IllegalStateException("Telephone must be provided if a contact is present")
                                )
            )
        )
      ),
      Seq.empty // TODO - build out incidents
    )

  // TODO incidents impl - from domain objects?
//  private def incidents(userAnswers: UserAnswers): Either[String, IncidentType01] =
//    for {
//      incidentsSection <- userAnswers.getOptional(IncidentsSection)
//      result <- {
//        incidentsSection match {
//          case Some(incident) => incident.validate[Seq[IncidentType01]]match {
//            case JsSuccess(incidents, _) => Right(incidents)
//            case JsError
//          }
//          case None =>
//        }
//      }
//    } yield result
//    } yield IncidentType01(
//      sequenceNumber = ???,
//      code = ???,
//      text = ???,
//      Endorsement = ???,
//      Location = ???,
//      TransportEquipment = ???,
//      Transhipment = ???
//    )

  private def getAddressNoPostcode(address: Option[DynamicAddress], country: Option[Country]): Option[AddressType14] =
    address.flatMap(
      a =>
        a.postalCode match {
          case Some(_) => None
          case _ =>
            Some(
              AddressType14(a.numberAndStreet,
                            None,
                            a.city,
                            country
                              .getOrElse(
                                throw new IllegalStateException("Country is required")
                              )
                              .code
                              .code
              )
            )
        }
    )

  private def getAddressWithPostcode(address: Option[DynamicAddress], country: Option[Country]): Option[PostcodeAddressType02] =
    address.flatMap(
      a =>
        a.postalCode match {
          case Some(postCode) =>
            Some(
              PostcodeAddressType02(Some(a.numberAndStreet),
                                    postCode,
                                    country
                                      .getOrElse(
                                        throw new IllegalStateException("Country is required")
                                      )
                                      .code
                                      .code
              )
            )
          case _ => None
        }
    )
}
