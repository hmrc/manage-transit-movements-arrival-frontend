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

package viewModels

import base.SpecBase
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.Mode
import models.locationOfGoods.QualifierOfIdentification._
import org.scalacheck.Arbitrary.arbitrary
import pages.locationOfGoods._
import viewModels.LocationOfGoodsAnswersViewModel.LocationOfGoodsAnswersViewModelProvider

class LocationOfGoodsAnswersViewModelSpec extends SpecBase with Generators with ArrivalUserAnswersGenerator {

  "must return section" - {
    "when CustomsOffice qualifier" in {
      val initialAnswers = emptyUserAnswers.setValue(QualifierOfIdentificationPage, CustomsOffice)

      forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
        (mode, answers) =>
          val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

          section.sectionTitle.get mustBe "Location of goods"
          section.rows.size mustBe 3
      }
    }

    "when EoriNumber qualifier" - {
      val qualifier = EoriNumber

      "with neither additional identifier nor contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, false)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 5
        }
      }

      "with additional identifier" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, true)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 6
        }
      }

      "with contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, false)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 7
        }
      }

      "with additional identifier and contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, true)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 8
        }
      }
    }

    "when AuthorisationNumber qualifier" - {
      val qualifier = AuthorisationNumber

      "with neither additional identifier nor contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, false)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 5
        }
      }

      "with additional identifier" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, true)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 6
        }
      }

      "with contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, false)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 7
        }
      }

      "with additional identifier and contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddAdditionalIdentifierPage, true)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 8
        }
      }
    }

    "when Coordinates qualifier" - {
      val qualifier = Coordinates

      "with no contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 4
        }
      }

      "with contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 6
        }
      }
    }

    "when Unlocode qualifier" - {
      val qualifier = Unlocode

      "with no contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 4
        }
      }

      "with contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 6
        }
      }
    }

    "when Address qualifier" - {
      val qualifier = Address

      "with no contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 4
        }
      }

      "with contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 6
        }
      }
    }

    "when PostalCode qualifier" - {
      val qualifier = PostalCode

      "with no contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, false)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 4
        }
      }

      "with contact person" in {
        val initialAnswers = emptyUserAnswers
          .setValue(QualifierOfIdentificationPage, qualifier)
          .setValue(AddContactPersonPage, true)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe "Location of goods"
            section.rows.size mustBe 6
        }
      }
    }
  }
}
