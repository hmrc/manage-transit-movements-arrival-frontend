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

import models.AddressLine.{City, NumberAndStreet, PostalCode, StreetNumber}
import models.domain.StringFieldRegex.{coordinatesLatitudeMaxRegex, coordinatesLongitudeMaxRegex}
import models.incident.IncidentCode
import models.reference._
import models.{QualifierOfIdentification, _}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import play.api.mvc.Call
import uk.gov.hmrc.http.HttpVerbs._
import wolfendale.scalacheck.regexp.RegexpGen

trait ModelGenerators {
  self: Generators =>

  implicit lazy val arbitraryIdentification: Arbitrary[models.incident.transportMeans.Identification] =
    Arbitrary {
      Gen.oneOf(models.incident.transportMeans.Identification.values.toSeq)
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
      Gen.oneOf(IncidentCode.values)
    }

  lazy val arbitrary3Or6IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(IncidentCode.TransferredToAnotherTransport, IncidentCode.UnexpectedlyChanged)
    }

  lazy val arbitrary2Or4IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(IncidentCode.SealsBrokenOrTampered, IncidentCode.PartiallyOrFullyUnloaded)
    }

  lazy val arbitrary1Or5IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(IncidentCode.DeviatedFromItinerary, IncidentCode.CarrierUnableToComply)
    }

  lazy val arbitraryNot3Or6IncidentCode: Arbitrary[IncidentCode] =
    Arbitrary {
      Gen.oneOf(
        IncidentCode.SealsBrokenOrTampered,
        IncidentCode.PartiallyOrFullyUnloaded,
        IncidentCode.DeviatedFromItinerary,
        IncidentCode.CarrierUnableToComply
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

  implicit lazy val arbitraryUnLocode: Arbitrary[UnLocode] =
    Arbitrary {
      for {
        unLocodeExtendedCode <- nonEmptyString
        name                 <- nonEmptyString
      } yield UnLocode(unLocodeExtendedCode, name)
    }

  implicit lazy val arbitraryCoordinates: Arbitrary[Coordinates] =
    Arbitrary {
      for {
        latitude  <- RegexpGen.from(coordinatesLatitudeMaxRegex)
        longitude <- RegexpGen.from(coordinatesLongitudeMaxRegex)
      } yield models.Coordinates(latitude, longitude)
    }

  implicit lazy val arbitraryTypeOfLocation: Arbitrary[models.locationOfGoods.TypeOfLocation] =
    Arbitrary {
      Gen.oneOf(models.locationOfGoods.TypeOfLocation.values)
    }

  implicit lazy val arbitraryQualifierOfIdentification: Arbitrary[QualifierOfIdentification] =
    Arbitrary {
      Gen.oneOf(QualifierOfIdentification.values)
    }

  lazy val arbitraryNonLocationQualifierOfIdentification: Arbitrary[QualifierOfIdentification] =
    Arbitrary {
      Gen.oneOf(QualifierOfIdentification.values diff QualifierOfIdentification.locationValues)
    }

  implicit lazy val arbitraryAuthorisationType: Arbitrary[models.identification.authorisation.AuthorisationType] =
    Arbitrary {
      Gen.oneOf(models.identification.authorisation.AuthorisationType.values)
    }

  implicit lazy val arbitraryProcedureType: Arbitrary[models.identification.ProcedureType] =
    Arbitrary {
      Gen.oneOf(models.identification.ProcedureType.values)
    }

  implicit lazy val arbitraryMovementReferenceNumber: Arbitrary[MovementReferenceNumber] =
    Arbitrary {
      for {
        year <- Gen
          .choose(0, 99)
          .map(
            y => f"$y%02d"
          )
        country <- Gen.pick(2, 'A' to 'Z')
        serial  <- Gen.pick(13, ('A' to 'Z') ++ ('0' to '9'))
      } yield MovementReferenceNumber(year, country.mkString, serial.mkString)
    }

  implicit lazy val arbitraryCustomsOffice: Arbitrary[CustomsOffice] =
    Arbitrary {
      for {
        id          <- nonEmptyString
        name        <- nonEmptyString
        phoneNumber <- Gen.option(Gen.alphaNumStr)
      } yield CustomsOffice(id, Some(name), phoneNumber)
    }

  implicit def arbitrarySelectableList[T <: Selectable](implicit arbitrary: Arbitrary[T]): Arbitrary[SelectableList[T]] = Arbitrary {
    for {
      values <- listWithMaxLength[T]()
    } yield SelectableList(values.distinctBy(_.value))
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

}
