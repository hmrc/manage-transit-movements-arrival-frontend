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

package models.journeyDomain.locationOfGoods

import cats.implicits._
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.locationOfGoods.QualifierOfIdentification
import models.locationOfGoods.QualifierOfIdentification._
import models.reference.CustomsOffice
import models.{InternationalAddress, UkAddress}
import pages.LocationOfGoods._

trait QualifierOfIdentificationDomain

object QualifierOfIdentificationDomain {

  implicit val userAnswersReader: UserAnswersReader[QualifierOfIdentificationDomain] =
    QualifierOfIdentificationPage.reader.flatMap {
      case QualifierOfIdentification.InternationalAddress => UserAnswersReader[InternationalAddressDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.IdentificationNumber => UserAnswersReader[IdentificationNumberDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.AuthorisationNumber  => UserAnswersReader[AuthorisationNumberDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.Coordinates          => UserAnswersReader[CoordinatesDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.CustomsOffice        => UserAnswersReader[CustomsOfficeDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.Unlocode             => UserAnswersReader[UnlocodeDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.Address              => UserAnswersReader[UKAddressDomain].widen[QualifierOfIdentificationDomain]
    }
}

case class InternationalAddressDomain(address: InternationalAddress, contactPerson: Option[ContactPerson]) extends QualifierOfIdentificationDomain

object InternationalAddressDomain {

  implicit val userAnswersReader: UserAnswersReader[InternationalAddressDomain] =
    (
      InternationalAddressPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPerson])
    ).tupled.map((InternationalAddressDomain.apply _).tupled)
}

case class IdentificationNumberDomain(eoriNumber: String, contactPerson: Option[ContactPerson], additionalIdentifier: String)
    extends QualifierOfIdentificationDomain

object IdentificationNumberDomain {

  implicit val userAnswersReader: UserAnswersReader[IdentificationNumberDomain] =
    (
      IdentificationNumberPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPerson]),
      AdditionalIdentifierPage.reader
    ).tupled.map((IdentificationNumberDomain.apply _).tupled)
}

case class AuthorisationNumberDomain(authorisationNumber: String, contactPerson: Option[ContactPerson], additionalIdentifier: String)
    extends QualifierOfIdentificationDomain

object AuthorisationNumberDomain {

  implicit val userAnswersReader: UserAnswersReader[AuthorisationNumberDomain] =
    (
      AuthorisationNumberPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPerson]),
      AdditionalIdentifierPage.reader
    ).tupled.map((AuthorisationNumberDomain.apply _).tupled)
}

case class CoordinatesDomain(coordinates: String, contactPerson: Option[ContactPerson]) extends QualifierOfIdentificationDomain

object CoordinatesDomain {

  implicit val userAnswersReader: UserAnswersReader[CoordinatesDomain] =
    (
      CoordinatesPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPerson])
    ).tupled.map((CoordinatesDomain.apply _).tupled)
}

case class CustomsOfficeDomain(customsOffice: CustomsOffice) extends QualifierOfIdentificationDomain

object CustomsOfficeDomain {

  implicit val userAnswersReader: UserAnswersReader[CustomsOfficeDomain] =
    CustomsOfficePage.reader.map(CustomsOfficeDomain(_))
}

case class UnlocodeDomain(code: String, contactPerson: Option[ContactPerson]) extends QualifierOfIdentificationDomain

object UnlocodeDomain {

  implicit val userAnswersReader: UserAnswersReader[UnlocodeDomain] =
    (
      UnlocodePage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPerson])
    ).tupled.map((UnlocodeDomain.apply _).tupled)
}

case class UKAddressDomain(address: UkAddress, contactPerson: Option[ContactPerson]) extends QualifierOfIdentificationDomain

object UKAddressDomain {

  implicit val userAnswersReader: UserAnswersReader[UKAddressDomain] =
    (
      AddressPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPerson])
    ).tupled.map((UKAddressDomain.apply _).tupled)
}
