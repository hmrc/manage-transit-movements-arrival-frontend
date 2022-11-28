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
import generators.Generators
import models.QualifierOfIdentification._
import models.{Mode, QualifierOfIdentification}
import org.scalacheck.Arbitrary.arbitrary
import pages.locationOfGoods._
import viewModels.LocationOfGoodsAnswersViewModel.LocationOfGoodsAnswersViewModelProvider

class LocationOfGoodsAnswersViewModelSpec extends SpecBase with Generators {

  private val sectionTitle = "Location of goods"

  "location of goods section" - {
    "when CustomsOffice qualifier" - {
      "must have 3 rows" in {
        val initialAnswers = emptyUserAnswers.setValue(QualifierOfIdentificationPage, QualifierOfIdentification.CustomsOffice)

        forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
          (mode, answers) =>
            val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

            section.sectionTitle.get mustBe sectionTitle
            section.rows.size mustBe 3
            section.addAnotherLink must not be defined
        }
      }
    }

    "when EoriNumber qualifier" - {
      val qualifier = QualifierOfIdentification.EoriNumber

      "with neither additional identifier nor contact person" - {
        "must have 5 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, false)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 5
              section.addAnotherLink must not be defined
          }
        }
      }

      "with additional identifier" - {
        "must have 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, true)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 6
              section.addAnotherLink must not be defined
          }
        }
      }

      "with contact person" - {
        "must have 7 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, false)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 7
              section.addAnotherLink must not be defined
          }
        }
      }

      "with additional identifier and contact person" - {
        "must have 8 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, true)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 8
              section.addAnotherLink must not be defined
          }
        }
      }
    }

    "when AuthorisationNumber qualifier" - {
      val qualifier = QualifierOfIdentification.AuthorisationNumber

      "with neither additional identifier nor contact person" - {
        "must have 5 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, false)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 5
              section.addAnotherLink must not be defined
          }
        }
      }

      "with additional identifier" - {
        "must have 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, true)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 6
              section.addAnotherLink must not be defined
          }
        }
      }

      "with contact person" - {
        "must have 7 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, false)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 7
              section.addAnotherLink must not be defined
          }
        }
      }

      "with additional identifier and contact person" - {
        "must have 8 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddAdditionalIdentifierPage, true)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 8
              section.addAnotherLink must not be defined
          }
        }
      }
    }

    "when Coordinates qualifier" - {
      val qualifier = QualifierOfIdentification.Coordinates

      "with no contact person" - {
        "must have 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 4
              section.addAnotherLink must not be defined
          }
        }
      }

      "with contact person" - {
        "must have 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 6
              section.addAnotherLink must not be defined
          }
        }
      }
    }

    "when Unlocode qualifier" - {
      val qualifier = QualifierOfIdentification.Unlocode

      "with no contact person" - {
        "must have 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 4
              section.addAnotherLink must not be defined
          }
        }
      }

      "with contact person" - {
        "must have 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 6
              section.addAnotherLink must not be defined
          }
        }
      }
    }

    "when Address qualifier" - {
      val qualifier = QualifierOfIdentification.Address

      "with no contact person" - {
        "must have 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 4
              section.addAnotherLink must not be defined
          }
        }
      }

      "with contact person" - {
        "must have 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 6
              section.addAnotherLink must not be defined
          }
        }
      }
    }

    "when PostalCode qualifier" - {
      val qualifier = QualifierOfIdentification.PostalCode

      "with no contact person" - {
        "must have 4 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, false)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 4
              section.addAnotherLink must not be defined
          }
        }
      }

      "with contact person" - {
        "must have 6 rows" in {
          val initialAnswers = emptyUserAnswers
            .setValue(QualifierOfIdentificationPage, qualifier)
            .setValue(AddContactPersonPage, true)

          forAll(arbitrary[Mode], arbitraryLocationOfGoodsAnswers(initialAnswers)) {
            (mode, answers) =>
              val section = new LocationOfGoodsAnswersViewModelProvider().apply(answers, mode).section

              section.sectionTitle.get mustBe sectionTitle
              section.rows.size mustBe 6
              section.addAnotherLink must not be defined
          }
        }
      }
    }
  }
}
