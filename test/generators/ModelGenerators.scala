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

package generators

import config.Constants.IncidentCode._
import config.PhaseConfig
import models.AddressLine.{City, NumberAndStreet, PostalCode, StreetNumber}
import models._
import models.LockCheck.*
import models.domain.StringFieldRegex.{coordinatesLatitudeMaxRegex, coordinatesLongitudeMaxRegex, mrnFinalRegex, mrnTransitionRegex}
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs._
import wolfendale.scalacheck.regexp.RegexpGen

import scala.util.matching.Regex

trait ModelGenerators {

  self: Generators =>

  implicit lazy val arbitraryIdentification: Arbitrary[Identification] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("10", "11", "20", "21", "30", "31", "40", "41", "80", "81", "99")
        description <- nonEmptyString
      } yield Identification(code, description)
    }

  implicit lazy val arbitraryDynamicAddress: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(NumberAndStreet.length, Gen.alphaNumChar)
        city            <- stringsWithMaxLength(City.length, Gen.alphaNumChar)
        postalCode      <- Gen.option(stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar))
      } yield DynamicAddress(numberAndStreet, city, postalCode)
    }

  lazy val arbitraryDynamicAddressWithRequiredPostalCode: Arbitrary[DynamicAddress] =
    Arbitrary {
      for {
        numberAndStreet <- stringsWithMaxLength(NumberAndStreet.length, Gen.alphaNumChar)
        city            <- stringsWithMaxLength(City.length, Gen.alphaNumChar)
        postalCode      <- stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar)
      } yield DynamicAddress(numberAndStreet, city, Some(postalCode))
    }

  implicit lazy val arbitraryIncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("1", "2", "3", "4", "5", "6")
        description <- nonEmptyString
      } yield IncidentCode(code, description)
    }

  lazy val arbitrary3Or6IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(IncidentCode(TransferredToAnotherTransportCode, "test"), IncidentCode(UnexpectedlyChangedCode, "test"))
    }

  lazy val arbitrary2Or4IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(IncidentCode(SealsBrokenOrTamperedCode, "test"), IncidentCode(PartiallyOrFullyUnloadedCode, "test"))
    }

  lazy val arbitrary1Or5IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(IncidentCode(DeviatedFromItineraryCode, "test"), IncidentCode(CarrierUnableToComplyCode, "test"))
    }

  lazy val arbitraryNot3Or6IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(
        IncidentCode(SealsBrokenOrTamperedCode, "test1"),
        IncidentCode(PartiallyOrFullyUnloadedCode, "test2"),
        IncidentCode(DeviatedFromItineraryCode, "test3"),
        IncidentCode(CarrierUnableToComplyCode, "test4")
      )
    }

  implicit lazy val arbitraryPostalCodeAddress: Arbitrary[PostalCodeAddress] =
    Arbitrary {
      for {
        streetNumber <- stringsWithMaxLength(StreetNumber.length, Gen.alphaNumChar)
        postalCode   <- stringsWithMaxLength(PostalCode.length, Gen.alphaNumChar)
        country      <- arbitrary[Country]
      } yield PostalCodeAddress(streetNumber, postalCode, country)
    }

  implicit lazy val arbitraryUnLocode: Arbitrary[String] =
    Arbitrary {
      for {
        code <- stringsWithExactLength(5, 5: Int)
      } yield code
    }

  implicit lazy val arbitraryCoordinates: Arbitrary[Coordinates] =
    Arbitrary {
      for {
        latitude  <- RegexpGen.from(coordinatesLatitudeMaxRegex)
        longitude <- RegexpGen.from(coordinatesLongitudeMaxRegex)
      } yield models.Coordinates(latitude, longitude)
    }

  implicit lazy val arbitraryTypeOfLocation: Arbitrary[TypeOfLocation] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("A", "B", "C", "D")
        description <- nonEmptyString
      } yield TypeOfLocation(code, description)
    }

  def qualifierOfIdentificationGen(qualifier: String): Gen[QualifierOfIdentification] =
    nonEmptyString.map(QualifierOfIdentification(qualifier, _))

  implicit lazy val arbitraryQualifierOfIdentification: Arbitrary[QualifierOfIdentification] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("T", "U", "V", "W", "X", "Y", "Z")
        description <- nonEmptyString
      } yield QualifierOfIdentification(code, description)
    }

  lazy val arbitraryNonLocationQualifierOfIdentification: Arbitrary[QualifierOfIdentification] =
    Arbitrary {
      for {
        code        <- Gen.oneOf("T", "V", "X", "Y")
        description <- nonEmptyString
      } yield QualifierOfIdentification(code, description)
    }

  implicit lazy val arbitraryProcedureType: Arbitrary[models.identification.ProcedureType] =
    Arbitrary {
      Gen.oneOf(models.identification.ProcedureType.values)
    }

  implicit def arbitraryMovementReferenceNumber(implicit phaseConfig: PhaseConfig): Arbitrary[MovementReferenceNumber] =
    phaseConfig.phase match {
      case Phase.Transition     => arbitraryMovementReferenceNumberTransition
      case Phase.PostTransition => arbitraryMovementReferenceNumberFinal
    }

  lazy val arbitraryMovementReferenceNumberTransition: Arbitrary[MovementReferenceNumber] =
    Arbitrary {
      movementReferenceNumberGen(mrnTransitionRegex)
    }

  lazy val arbitraryMovementReferenceNumberFinal: Arbitrary[MovementReferenceNumber] =
    Arbitrary {
      movementReferenceNumberGen(mrnFinalRegex)
    }

  private def movementReferenceNumberGen(regex: Regex): Gen[MovementReferenceNumber] =
    RegexpGen.from(regex.toString()).flatMap {
      case mrn @ regex(year, countryCode, serial, _) =>
        val checkCharacter = MovementReferenceNumber.getCheckCharacter(year, countryCode, serial)
        new MovementReferenceNumber(s"${mrn.dropRight(1)}$checkCharacter")
      case _ =>
        movementReferenceNumberGen(regex)
    }

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id          <- nonEmptyString
        name        <- nonEmptyString
        phoneNumber <- Gen.option(Gen.alphaNumStr)
        countryId   <- nonEmptyString
      } yield CustomsOffice(id, name, phoneNumber, countryId)
    }

  implicit def arbitrarySelectableList[T <: Selectable](implicit arbitrary: Arbitrary[T]): Arbitrary[SelectableList[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield SelectableList(values.distinctBy(_.value))
  }

  implicit def arbitraryRadioableList[T <: Radioable[T]](implicit arbitrary: Arbitrary[T]): Arbitrary[Seq[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield values.distinctBy(_.code)
  }

  implicit lazy val arbitraryEoriNumber: Arbitrary[EoriNumber] =
    Arbitrary {
      for {
        number <- stringsWithMaxLength(forms.Constants.maxEoriNumberLength)
      } yield EoriNumber(number)
    }

  implicit lazy val arbitraryCountryCode: Arbitrary[CountryCode] =
    Arbitrary {
      Gen
        .pick(CountryCode.Constants.countryCodeLength, 'A' to 'Z')
        .map(
          code => CountryCode(code.mkString)
        )
    }

  implicit lazy val arbitraryCountry: Arbitrary[Country] =
    Arbitrary {
      for {
        code <- arbitrary[CountryCode]
        name <- nonEmptyString
      } yield Country(code, name)
    }

  implicit lazy val arbitraryMode: Arbitrary[Mode] = Arbitrary {
    Gen.oneOf(NormalMode, CheckMode)
  }

  implicit lazy val arbitraryCall: Arbitrary[Call] = Arbitrary {
    for {
      method <- Gen.oneOf(GET, POST)
      url    <- nonEmptyString
    } yield Call(method, url)
  }

  implicit lazy val arbitraryNationality: Arbitrary[Nationality] =
    Arbitrary {
      for {
        code <- nonEmptyString
        desc <- nonEmptyString
      } yield Nationality(code, desc)
    }

  implicit lazy val arbitraryLockCheck: Arbitrary[LockCheck] =
    Arbitrary {
      Gen.oneOf(Locked, Unlocked, LockCheckFailure)
    }
}
