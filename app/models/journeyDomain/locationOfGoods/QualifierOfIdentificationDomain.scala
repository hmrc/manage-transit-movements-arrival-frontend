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

package models.journeyDomain.locationOfGoods

import config.Constants.QualifierCode.*
import models.identification.ProcedureType
import models.journeyDomain.*
import models.reference.QualifierOfIdentification.*
import models.reference.{Country, CustomsOffice}
import models.{Coordinates, DynamicAddress}
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods.*

trait QualifierOfIdentificationDomain extends JourneyDomainModel {
  val contactPerson: Option[ContactPersonDomain]
}

object QualifierOfIdentificationDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    IsSimplifiedProcedurePage.readerNoAppend.to {
      case ProcedureType.Simplified =>
        AuthorisationNumberDomain.userAnswersReader
      case ProcedureType.Normal =>
        QualifierOfIdentificationPage.reader.to {
          _.code match {
            case AddressCode             => AddressDomain.userAnswersReader
            case EoriNumberCode          => EoriNumberDomain.userAnswersReader
            case AuthorisationNumberCode => AuthorisationNumberDomain.userAnswersReader
            case CoordinatesCode         => CoordinatesDomain.userAnswersReader
            case CustomsOfficeCode       => CustomsOfficeDomain.userAnswersReader
            case UnlocodeCode            => UnlocodeDomain.userAnswersReader
            case code                    => throw new Exception(s"Unexpected qualifier code $code")
          }
        }
    }
}

case class AddressDomain(country: Country, address: DynamicAddress, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object AddressDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    (
      CountryPage.reader,
      AddressPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(ContactPersonDomain.userAnswersReader)
    ).map(AddressDomain.apply)
}

case class EoriNumberDomain(eoriNumber: String, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object EoriNumberDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    (
      IdentificationNumberPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(ContactPersonDomain.userAnswersReader)
    ).map(EoriNumberDomain.apply)
}

case class AuthorisationNumberDomain(authorisationNumber: String, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object AuthorisationNumberDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    (
      AuthorisationNumberPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(ContactPersonDomain.userAnswersReader)
    ).map(AuthorisationNumberDomain.apply)
}

case class CoordinatesDomain(coordinates: Coordinates, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object CoordinatesDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    (
      CoordinatesPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(ContactPersonDomain.userAnswersReader)
    ).map(CoordinatesDomain.apply)
}

case class CustomsOfficeDomain(customsOffice: CustomsOffice) extends QualifierOfIdentificationDomain {
  override val contactPerson: Option[ContactPersonDomain] = None
}

object CustomsOfficeDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    CustomsOfficePage.reader.map(CustomsOfficeDomain(_))
}

case class UnlocodeDomain(code: String, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object UnlocodeDomain {

  implicit val userAnswersReader: Read[QualifierOfIdentificationDomain] =
    (
      UnlocodePage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(ContactPersonDomain.userAnswersReader)
    ).map(UnlocodeDomain.apply)
}
