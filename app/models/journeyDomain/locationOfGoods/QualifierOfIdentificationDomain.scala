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

import cats.implicits._
import forms.Constants._
import models.identification.ProcedureType
import models.journeyDomain.{GettableAsFilterForNextReaderOps, GettableAsReaderOps, UserAnswersReader}
import models.reference.QualifierOfIdentification._
import models.reference.{Country, CustomsOffice}
import models.{Coordinates, DynamicAddress, PostalCodeAddress}
import pages.identification.IsSimplifiedProcedurePage
import pages.locationOfGoods._

trait QualifierOfIdentificationDomain {
  val contactPerson: Option[ContactPersonDomain]
}

object QualifierOfIdentificationDomain {

  implicit val userAnswersReader: UserAnswersReader[QualifierOfIdentificationDomain] =
    IsSimplifiedProcedurePage.reader.flatMap {
      case ProcedureType.Simplified =>
        UserAnswersReader[AuthorisationNumberDomain].widen[QualifierOfIdentificationDomain]
      case ProcedureType.Normal =>
        QualifierOfIdentificationPage.reader.map(_.code).flatMap {
          case AddressCode             => UserAnswersReader[AddressDomain].widen[QualifierOfIdentificationDomain]
          case EoriNumberCode          => UserAnswersReader[EoriNumberDomain].widen[QualifierOfIdentificationDomain]
          case AuthorisationNumberCode => UserAnswersReader[AuthorisationNumberDomain].widen[QualifierOfIdentificationDomain]
          case CoordinatesCode         => UserAnswersReader[CoordinatesDomain].widen[QualifierOfIdentificationDomain]
          case CustomsOfficeCode       => UserAnswersReader[CustomsOfficeDomain].widen[QualifierOfIdentificationDomain]
          case UnlocodeCode            => UserAnswersReader[UnlocodeDomain].widen[QualifierOfIdentificationDomain]
          case PostalCodeCode          => UserAnswersReader[PostalCodeDomain].widen[QualifierOfIdentificationDomain]
          case code                    => throw new Exception(s"Unexpected qualifier code $code")
        }
    }
}

case class AddressDomain(country: Country, address: DynamicAddress, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object AddressDomain {

  implicit val userAnswersReader: UserAnswersReader[AddressDomain] =
    (
      CountryPage.reader,
      AddressPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((AddressDomain.apply _).tupled)
}

case class EoriNumberDomain(eoriNumber: String, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object EoriNumberDomain {

  implicit val userAnswersReader: UserAnswersReader[EoriNumberDomain] =
    (
      IdentificationNumberPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((EoriNumberDomain.apply _).tupled)
}

case class AuthorisationNumberDomain(authorisationNumber: String, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object AuthorisationNumberDomain {

  implicit val userAnswersReader: UserAnswersReader[AuthorisationNumberDomain] =
    (
      AuthorisationNumberPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((AuthorisationNumberDomain.apply _).tupled)
}

case class CoordinatesDomain(coordinates: Coordinates, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object CoordinatesDomain {

  implicit val userAnswersReader: UserAnswersReader[CoordinatesDomain] =
    (
      CoordinatesPage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((CoordinatesDomain.apply _).tupled)
}

case class CustomsOfficeDomain(customsOffice: CustomsOffice) extends QualifierOfIdentificationDomain {
  override val contactPerson: Option[ContactPersonDomain] = None
}

object CustomsOfficeDomain {

  implicit val userAnswersReader: UserAnswersReader[CustomsOfficeDomain] =
    CustomsOfficePage.reader.map(CustomsOfficeDomain(_))
}

case class UnlocodeDomain(code: String, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object UnlocodeDomain {

  implicit val userAnswersReader: UserAnswersReader[UnlocodeDomain] =
    (
      UnlocodePage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((UnlocodeDomain.apply _).tupled)
}

case class PostalCodeDomain(address: PostalCodeAddress, contactPerson: Option[ContactPersonDomain]) extends QualifierOfIdentificationDomain

object PostalCodeDomain {

  implicit val userAnswersReader: UserAnswersReader[PostalCodeDomain] =
    (
      PostalCodePage.reader,
      AddContactPersonPage.filterOptionalDependent(identity)(UserAnswersReader[ContactPersonDomain])
    ).tupled.map((PostalCodeDomain.apply _).tupled)
}
