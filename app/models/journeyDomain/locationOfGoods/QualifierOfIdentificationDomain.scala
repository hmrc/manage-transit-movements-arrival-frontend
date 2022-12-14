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
import models.QualifierOfIdentification._
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.reference.{Country, CustomsOffice, UnLocode}
import models.{Coordinates, DynamicAddress, PostalCodeAddress, QualifierOfIdentification}
import pages.locationOfGoods._

trait QualifierOfIdentificationDomain {
  val qualifierOfIdentification: String
  val contactPerson: Option[ContactPersonDomain]
}

object QualifierOfIdentificationDomain {

  implicit val userAnswersReader: UserAnswersReader[QualifierOfIdentificationDomain] =
    QualifierOfIdentificationPage.reader.flatMap {
      case QualifierOfIdentification.Address             => UserAnswersReader[AddressDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.EoriNumber          => UserAnswersReader[EoriNumberDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.AuthorisationNumber => UserAnswersReader[AuthorisationNumberDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.Coordinates         => UserAnswersReader[CoordinatesDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.CustomsOffice       => UserAnswersReader[CustomsOfficeDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.Unlocode            => UserAnswersReader[UnlocodeDomain].widen[QualifierOfIdentificationDomain]
      case QualifierOfIdentification.PostalCode          => UserAnswersReader[PostalCodeDomain].widen[QualifierOfIdentificationDomain]
    }
}

case class AddressDomain(country: Country, address: DynamicAddress, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String = "Z"
}

object AddressDomain {

  implicit val userAnswersReader: UserAnswersReader[AddressDomain] =
    (
      CountryPage.reader,
      AddressPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((AddressDomain.apply _).tupled)
}

case class EoriNumberDomain(eoriNumber: String, additionalIdentifier: Option[String], contactPerson: Option[ContactPersonDomain])
    extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String = "X"
}

object EoriNumberDomain {

  implicit val userAnswersReader: UserAnswersReader[EoriNumberDomain] =
    (
      IdentificationNumberPage.reader,
      AddAdditionalIdentifierPage.filterOptionalDependent(identity)(AdditionalIdentifierPage.reader),
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((EoriNumberDomain.apply _).tupled)
}

case class AuthorisationNumberDomain(authorisationNumber: String, additionalIdentifier: Option[String], contactPerson: Option[ContactPersonDomain])
    extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String = "Y"
}

object AuthorisationNumberDomain {

  implicit val userAnswersReader: UserAnswersReader[AuthorisationNumberDomain] =
    (
      AuthorisationNumberPage.reader,
      AddAdditionalIdentifierPage.filterOptionalDependent(identity)(AdditionalIdentifierPage.reader),
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((AuthorisationNumberDomain.apply _).tupled)
}

case class CoordinatesDomain(coordinates: Coordinates, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String = "W"
}

object CoordinatesDomain {

  implicit val userAnswersReader: UserAnswersReader[CoordinatesDomain] =
    (
      CoordinatesPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((CoordinatesDomain.apply _).tupled)
}

case class CustomsOfficeDomain(customsOffice: CustomsOffice) extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String          = "V"
  override val contactPerson: Option[ContactPersonDomain] = None
}

object CustomsOfficeDomain {

  implicit val userAnswersReader: UserAnswersReader[CustomsOfficeDomain] =
    CustomsOfficePage.reader.map(CustomsOfficeDomain(_))
}

case class UnlocodeDomain(code: UnLocode, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String = "U"
}

object UnlocodeDomain {

  implicit val userAnswersReader: UserAnswersReader[UnlocodeDomain] =
    (
      UnlocodePage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((UnlocodeDomain.apply _).tupled)
}

case class PostalCodeDomain(address: PostalCodeAddress, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain {
  override val qualifierOfIdentification: String = "T"
}

object PostalCodeDomain {

  implicit val userAnswersReader: UserAnswersReader[PostalCodeDomain] =
    (
      PostalCodePage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((PostalCodeDomain.apply _).tupled)
}
