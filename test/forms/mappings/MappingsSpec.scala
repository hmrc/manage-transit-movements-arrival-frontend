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

package forms.mappings

import base.SpecBase
import generators.Generators
import models.{Enumerable, MovementReferenceNumber, Radioable, Selectable, SelectableList}
import org.scalacheck.Arbitrary.arbitrary
import play.api.data.{Form, FormError}

class MappingsSpec extends SpecBase with Mappings with Generators {

  "text" - {

    val testForm: Form[String] =
      Form(
        "value" -> text()
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "must bind a valid string with trailing whitespace" in {
      val result = testForm.bind(Map("value" -> "foobar   "))
      result.get mustEqual "foobar"
    }

    "must bind a valid string with leading and trailing spaces" in {
      val result = testForm.bind(Map("value" -> "  foobar     "))
      result.get mustEqual "foobar"
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a string with spaces" in {
      val result = testForm.bind(Map("value" -> "      "))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must return a custom error message" in {
      val form   = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "boolean" - {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean()
      )

    "must bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "must bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "must not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" - {

    val testForm: Form[Int] =
      Form(
        "value" -> int()
      )

    "must bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "must bind a valid integer with comma separators" in {
      val result = testForm.bind(Map("value" -> "1,000"))
      result.get mustEqual 1000
    }

    "must bind a valid integer spaces" in {
      val result = testForm.bind(Map("value" -> "1 000 000"))
      result.get mustEqual 1000000
    }

    "must not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" - {

    sealed trait Foo extends Radioable[Foo]
    case object Bar extends Foo {
      override val code: String             = "bar"
      override val messageKeyPrefix: String = "mk.bar"
    }
    case object Baz extends Foo {
      override val code: String             = "baz"
      override val messageKeyPrefix: String = "mk.baz"
    }

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(
        Seq(Bar, Baz)
          .map(
            v => v.toString -> v
          )*
      )

    val testForm = Form(
      "value" -> enumerable[Foo]()
    )

    "must bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "must not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(Bar)
      result.apply("value").value.value mustEqual "bar"
    }
  }

  "mrn" - {

    def testFormBuilder = Form(
      "value" -> mrn("error.required", "error.length", "error.invalid.invalidCharacter", "error.invalidMRN")
    )

    val testForm = testFormBuilder

    "must bind valid MRNs" in {

      forAll(arbitrary[MovementReferenceNumber](arbitraryMovementReferenceNumber)) {
        mrn =>
          val result = testForm.bind(Map("value" -> mrn.toString))
          result.get mustEqual mrn
      }
    }

    "must not bind invalid MRNs" - {

      "must not bind when value is longer than max length" in {

        forAll(stringsLongerThan(MovementReferenceNumber.Constants.length)) {
          invalidMrn =>
            val result = testForm.bind(Map("value" -> invalidMrn))
            result.errors must contain(FormError("value", "error.length"))
        }
      }

      "must not bind when value is shorter than max length" in {

        forAll(stringsWithMaxLength(MovementReferenceNumber.Constants.length - 1)) {
          invalidMrn =>
            val result = testForm.bind(Map("value" -> invalidMrn))
            result.errors must contain(FormError("value", "error.length"))
        }
      }

      "must not bind when value contains an invalid character" in {
        forAll(stringsWithLength(MovementReferenceNumber.Constants.length - 1)) {
          value =>
            val valueStartingWithUnderscore = s"_$value"
            val result                      = testForm.bind(Map("value" -> valueStartingWithUnderscore))
            result.errors must contain(FormError("value", "error.invalid.invalidCharacter"))
        }
      }

      "must not bind when value is not a valid MRN" in {

        forAll(alphaStringWithLength(MovementReferenceNumber.Constants.length)) {
          invalidMrn =>
            val result = testForm.bind(Map("value" -> invalidMrn))
            result.errors must contain(FormError("value", "error.invalidMRN"))
        }
      }
    }

  }

  "mrnUnsafe" - {

    val testForm = Form(
      "value" -> mrnUnsafe("error.required", "error.length", "error.invalid.invalidCharacter", "error.invalidMRN")
    )

    "must bind valid MRNs" in {

      forAll(arbitrary[MovementReferenceNumber](arbitraryMovementReferenceNumber)) {
        mrn =>
          val result = testForm.bind(Map("value" -> mrn.toString))
          result.get mustEqual mrn
      }

    }

    "must not bind invalid MRNs" - {

      "must not bind when value is longer than max length" in {

        forAll(stringsLongerThan(MovementReferenceNumber.Constants.length)) {
          invalidMrn =>
            val result = testForm.bind(Map("value" -> invalidMrn))
            result.errors must contain(FormError("value", "error.length"))
        }
      }

      "must not bind when value is shorter than max length" in {

        forAll(stringsWithMaxLength(MovementReferenceNumber.Constants.length - 1)) {
          invalidMrn =>
            val result = testForm.bind(Map("value" -> invalidMrn))
            result.errors must contain(FormError("value", "error.length"))
        }
      }

      "must not bind when value contains an invalid character" in {
        forAll(stringsWithLength(MovementReferenceNumber.Constants.length - 1)) {
          value =>
            val valueStartingWithUnderscore = s"_$value"
            val result                      = testForm.bind(Map("value" -> valueStartingWithUnderscore))
            result.errors must contain(FormError("value", "error.invalid.invalidCharacter"))
        }
      }

      "must not bind when value is not a valid MRN" in {

        forAll(alphaStringWithLength(MovementReferenceNumber.Constants.length)) {
          invalidMrn =>
            val result = testForm.bind(Map("value" -> invalidMrn))
            result.errors must contain(FormError("value", "error.invalidMRN"))
        }
      }
    }

  }

  "selectable" - {

    case class Foo(value: String) extends Selectable

    val foo            = Foo("foo")
    val selectableList = SelectableList(Seq(foo))

    val testForm: Form[Foo] =
      Form(
        "value" -> selectable[Foo](selectableList)
      )

    "must bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foo"))
      result.get mustEqual foo
    }

    "must not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "must not bind a country not in the list" in {
      val result = testForm.bind(Map("value" -> "FR"))
      result.errors must contain(FormError("value", "error.required"))
    }

    "must unbind a valid value" in {
      val result = testForm.fill(foo)
      result.apply("value").value.value mustEqual "foo"
    }
  }
}
