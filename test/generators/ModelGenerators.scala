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

package generators

import models._
import models.reference._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {
  self: Generators =>

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
        id          <- arbitrary[String]
        name        <- arbitrary[Option[String]]
        phoneNumber <- Gen.option(arbitrary[String])
      } yield CustomsOffice(id, name, phoneNumber)
    }

  implicit lazy val arbitraryCustomsOfficeList: Arbitrary[CustomsOfficeList] =
    Arbitrary {
      for {
        customsOffices <- listWithMaxLength[CustomsOffice]()
      } yield CustomsOfficeList(customsOffices)
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
        name <- arbitrary[String]
      } yield Country(code, name)
    }

  implicit lazy val arbitraryMode: Arbitrary[Mode] = Arbitrary {
    Gen.oneOf(NormalMode, CheckMode)
  }

  implicit lazy val arbitraryCountryList: Arbitrary[CountryList] = Arbitrary {
    for {
      countries <- listWithMaxLength[Country]()
    } yield CountryList(countries)
  }

  implicit lazy val arbitraryInternationalAddress: Arbitrary[Address] =
    Arbitrary {
      for {
        addressLine1 <- stringsWithMaxLength(AddressLine.AddressLine1.length, Gen.alphaNumChar)
        addressLine2 <- stringsWithMaxLength(AddressLine.AddressLine2.length, Gen.alphaNumChar)
        postalCode   <- stringsWithMaxLength(AddressLine.PostalCode.length, Gen.alphaNumChar)
        country      <- arbitrary[Country]
      } yield InternationalAddress(addressLine1, addressLine2, postalCode, country)
    }

  implicit lazy val arbitraryUkAddress: Arbitrary[UkAddress] =
    Arbitrary {
      for {
        addressLine1 <- stringsWithMaxLength(AddressLine.AddressLine1.length, Gen.alphaNumChar)
        addressLine2 <- stringsWithMaxLength(AddressLine.AddressLine2.length, Gen.alphaNumChar)
        postCode     <- stringsWithMaxLength(AddressLine.UkPostCode.length, Gen.alphaNumChar)
      } yield UkAddress(addressLine1, addressLine2, postCode)
    }
}
