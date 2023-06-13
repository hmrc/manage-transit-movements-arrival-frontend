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

package models.locationOfGoods

import base.SpecBase
import models.identification.ProcedureType._
import models.locationOfGoods.TypeOfLocation._
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.identification.IsSimplifiedProcedurePage
import play.api.libs.json.{JsError, JsString, Json}

class TypeOfLocationSpec extends SpecBase with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "Typeoflocation" - {

    "must deserialise valid values" in {

      val gen = Gen.oneOf(TypeOfLocation.values)

      forAll(gen) {
        typeoflocation =>
          JsString(typeoflocation.toString).validate[TypeOfLocation].asOpt.value mustEqual typeoflocation
      }
    }

    "must fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!TypeOfLocation.values.map(_.toString).contains(_))

      forAll(gen) {
        invalidValue =>
          JsString(invalidValue).validate[TypeOfLocation] mustEqual JsError("error.invalid")
      }
    }

    "must serialise" in {

      val gen = Gen.oneOf(TypeOfLocation.values)

      forAll(gen) {
        typeoflocation =>
          Json.toJson(typeoflocation) mustEqual JsString(typeoflocation.toString)
      }
    }

    "Radio options" - {

      "Must return the correct number of radios" - {
        "When procedure type is Simplified" in {
          val answers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, Simplified)

          val radios   = TypeOfLocation.values(answers)
          val expected = Seq(AuthorisedPlace, DesignatedLocation, ApprovedPlace, Other)
          radios mustBe expected
        }

        "When procedure type is Normal" in {
          val answers = emptyUserAnswers
            .setValue(IsSimplifiedProcedurePage, Normal)

          val radios   = TypeOfLocation.values(answers)
          val expected = Seq(DesignatedLocation, ApprovedPlace, Other)
          radios mustBe expected
        }
      }
    }
  }
}
