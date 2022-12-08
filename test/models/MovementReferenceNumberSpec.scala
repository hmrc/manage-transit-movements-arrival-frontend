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

package models

import base.SpecBase
import generators.Generators
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.EitherValues
import play.api.libs.json.{JsString, Json}
import play.api.mvc.PathBindable

class MovementReferenceNumberSpec extends SpecBase with Generators with EitherValues {

  "a Movement Reference Number" - {

    val pathBindable = implicitly[PathBindable[MovementReferenceNumber]]

    "must bind from a url" in {

      val mrn = MovementReferenceNumber("64NFNDKVOKT4K70SM7")

      val result = pathBindable.bind("mrn", "64NFNDKVOKT4K70SM7")

      result.value mustEqual mrn.value
    }

    "must deserialise" in {

      forAll(arbitrary[MovementReferenceNumber]) {
        mrn =>
          JsString(mrn.toString).as[MovementReferenceNumber] mustEqual mrn
      }
    }

    "must serialise" in {

      forAll(arbitrary[MovementReferenceNumber]) {
        mrn =>
          Json.toJson(mrn) mustEqual JsString(mrn.toString)
      }
    }

    "must fail to bind from a string that isn't 18 characters long" in {

      val gen = for {
        digits <- Gen.choose[Int](1, 30).suchThat(_ != 18)
        value  <- Gen.pick(digits, ('A' to 'Z') ++ ('0' to '9'))
      } yield value.mkString

      forAll(gen) {
        invalidMrn =>
          MovementReferenceNumber(invalidMrn) must not be defined
      }
    }

    "must fail to bind from a string that has any characters which aren't upper case or digits" in {

      val gen: Gen[(MovementReferenceNumber, Int, Char)] = for {
        mrn       <- arbitrary[MovementReferenceNumber]
        index     <- Gen.choose(0, 17)
        character <- arbitrary[Char]
      } yield (mrn, index, character)

      forAll(gen) {
        case (mrn, index, character) =>
          val validCharacters = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ"

          whenever(!validCharacters.contains(character)) {

            val invalidMrn = mrn.toString.updated(index, character)

            MovementReferenceNumber(invalidMrn) must not be defined
          }
      }
    }

    "must fail to bind from a string that does not have a digit as the first characters" in {

      forAll(arbitrary[MovementReferenceNumber], Gen.alphaUpperChar) {
        (mrn, upperCaseChar) =>
          val invalidMrn = mrn.toString.updated(0, upperCaseChar)

          MovementReferenceNumber(invalidMrn) must not be defined
      }
    }

    "must fail to bind from a string that does not have a digit as the second characters" in {

      forAll(arbitrary[MovementReferenceNumber], Gen.alphaUpperChar) {
        (mrn, upperCaseChar) =>
          val invalidMrn = mrn.toString.updated(1, upperCaseChar)

          MovementReferenceNumber(invalidMrn) must not be defined
      }
    }

    "must fail to bind a string that does not have an upper case character as the third character" in {

      forAll(arbitrary[MovementReferenceNumber], Gen.numChar) {
        (mrn, digit) =>
          val invalidMrn = mrn.toString.updated(2, digit)

          MovementReferenceNumber(invalidMrn) must not be defined
      }
    }

    "must fail to bind a string that does not have an upper case character as the fourth character" in {

      forAll(arbitrary[MovementReferenceNumber], Gen.numChar) {
        (mrn, digit) =>
          val invalidMrn = mrn.toString.updated(3, digit)

          MovementReferenceNumber(invalidMrn) must not be defined
      }
    }

    "must not build from a valid legacy P4 MRN" in {

      MovementReferenceNumber("99IT9876AB88901209") mustBe None
      MovementReferenceNumber("18GB0000601001EB15") mustBe None
      MovementReferenceNumber("18GB0000601001EBD1") mustBe None
      MovementReferenceNumber("18IT02110010006A10") mustBe None
      MovementReferenceNumber("18IT021100100069F4") mustBe None
      MovementReferenceNumber("18GB0000601001EBB5") mustBe None
    }

    "must build from a valid P5 MRN" - {

      "when GB MRN" in {

        MovementReferenceNumber("24GB1FR25DIMDJF1M4").value mustEqual MovementReferenceNumber("24", "GB", "1FR25DIMDJF1", "M")
        MovementReferenceNumber("29GBQHFCG83AJEB0K1").value mustEqual MovementReferenceNumber("29", "GB", "QHFCG83AJEB0", "K")
        MovementReferenceNumber("24GBXGY3OS021F7BL6").value mustEqual MovementReferenceNumber("24", "GB", "XGY3OS021F7B", "L")
        MovementReferenceNumber("27GBFN4IVUIDWI3SJ3").value mustEqual MovementReferenceNumber("27", "GB", "FN4IVUIDWI3S", "J")
        MovementReferenceNumber("28GBMK4FBCA8TSVQK0").value mustEqual MovementReferenceNumber("28", "GB", "MK4FBCA8TSVQ", "K")
      }

      "when XI MRN" in {

        MovementReferenceNumber("25XIKQDYH3A6DLFFL0").value mustEqual MovementReferenceNumber("25", "XI", "KQDYH3A6DLFF", "L")
        MovementReferenceNumber("28XI5WOLUSGUJGNEM3").value mustEqual MovementReferenceNumber("28", "XI", "5WOLUSGUJGNE", "M")
        MovementReferenceNumber("24XIFHAHUUPWDLWLM4").value mustEqual MovementReferenceNumber("24", "XI", "FHAHUUPWDLWL", "M")
        MovementReferenceNumber("24XISXD4QHSFPAKGJ8").value mustEqual MovementReferenceNumber("24", "XI", "SXD4QHSFPAKG", "J")
        MovementReferenceNumber("29XIPGOACVNEDKNGK6").value mustEqual MovementReferenceNumber("29", "XI", "PGOACVNEDKNG", "K")
      }

      "when random MRN" in {

        MovementReferenceNumber("24JYQDJQPLYSUMP4M1").value mustEqual MovementReferenceNumber("24", "JY", "QDJQPLYSUMP4", "M")
        MovementReferenceNumber("27RUNUC6ZY3D45ERJ2").value mustEqual MovementReferenceNumber("27", "RU", "NUC6ZY3D45ER", "J")
        MovementReferenceNumber("28WTRCT5JYVRKLECK7").value mustEqual MovementReferenceNumber("28", "WT", "RCT5JYVRKLEC", "K")
        MovementReferenceNumber("28YEOUDY04UAEFM8K3").value mustEqual MovementReferenceNumber("28", "YE", "OUDY04UAEFM8", "K")
        MovementReferenceNumber("28GZ10PU0ITMV1TBJ4").value mustEqual MovementReferenceNumber("28", "GZ", "10PU0ITMV1TB", "J")

        MovementReferenceNumber("88KIFZKXMEQ9XEMEK2").value mustEqual MovementReferenceNumber("88", "KI", "FZKXMEQ9XEME", "K")
        MovementReferenceNumber("42LWUQOH5LI0ZR44J7").value mustEqual MovementReferenceNumber("42", "LW", "UQOH5LI0ZR44", "J")
        MovementReferenceNumber("48RRNN8C8YPDVKUXM3").value mustEqual MovementReferenceNumber("48", "RR", "NN8C8YPDVKUX", "M")
        MovementReferenceNumber("91KV6RYHUKGGSQXWL3").value mustEqual MovementReferenceNumber("91", "KV", "6RYHUKGGSQXW", "L")
        MovementReferenceNumber("39NDRTTOXOAS2IJCK5").value mustEqual MovementReferenceNumber("39", "ND", "RTTOXOAS2IJC", "K")
        MovementReferenceNumber("38DFIPMZGTAM9TPWK4").value mustEqual MovementReferenceNumber("38", "DF", "IPMZGTAM9TPW", "K")
        MovementReferenceNumber("77QCCUFGXJSQHVKDM2").value mustEqual MovementReferenceNumber("77", "QC", "CUFGXJSQHVKD", "M")
        MovementReferenceNumber("63CC7SE1ZHX62XJEL7").value mustEqual MovementReferenceNumber("63", "CC", "7SE1ZHX62XJE", "L")
        MovementReferenceNumber("97NJWKXI6PTY3JYLM2").value mustEqual MovementReferenceNumber("97", "NJ", "WKXI6PTY3JYL", "M")
        MovementReferenceNumber("35UMHKWMX5YVGTNQK3").value mustEqual MovementReferenceNumber("35", "UM", "HKWMX5YVGTNQ", "K")
      }
    }

    "must treat .apply and .toString as dual" in {

      forAll(arbitrary[MovementReferenceNumber]) {
        mrn =>
          MovementReferenceNumber(mrn.toString).value mustEqual mrn
      }
    }

    "must fail to bind from inputs with invalid check characters" in {

      val checkDigitPosition = 17

      val gen = for {
        mrn               <- arbitrary[MovementReferenceNumber].map(_.toString)
        invalidCheckDigit <- Gen.alphaChar suchThat (_ != mrn(checkDigitPosition))
      } yield (mrn, invalidCheckDigit)

      forAll(gen) {
        case (mrn, invalidCheckDigit) =>
          val invalidMrn = mrn.updated(checkDigitPosition, invalidCheckDigit)

          MovementReferenceNumber(invalidMrn) must not be defined
      }
    }
  }
}
