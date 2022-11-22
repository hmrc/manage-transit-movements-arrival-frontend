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

package models.journeyDomain.incident

import base.SpecBase
import forms.Constants
import generators.{ArrivalUserAnswersGenerator, Generators}
import models.incident.IncidentCode
import models.journeyDomain.{EitherType, UserAnswersReader}
import models.reference.Country
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import pages.incident._

class IncidentDomainSpec extends SpecBase with ScalaCheckPropertyChecks with Generators with ArrivalUserAnswersGenerator {

  private val country      = arbitrary[Country].sample.value
  private val incidentCode = arbitrary[IncidentCode].sample.value
  private val incidentText = Gen.alphaNumStr.sample.value.take(Constants.maxIncidentTextLength)

  private val incidentCode3Or6Gen = Gen.oneOf(
    IncidentCode.TransferredToAnotherTransport,
    IncidentCode.UnexpectedlyChanged
  )

  private val incidentCodeNot3Or6Gen = Gen.oneOf(
    IncidentCode.SealsBrokenOrTampered,
    IncidentCode.PartiallyOrFullyUnloaded,
    IncidentCode.DeviatedFromItinerary,
    IncidentCode.CarrierUnableToComply
  )

  "IncidentDomain" - {

    "when incident code is 3 or 6" - {
      "transport means must be defined" in {
        forAll(incidentCode3Or6Gen) {
          incidentCode =>
            val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)

            forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex)) {
              userAnswers =>
                val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](
                  IncidentDomain.userAnswersReader(index)
                ).run(userAnswers)

                result.value.transportMeans must be(defined)
            }
        }
      }
    }

    "when incident code is not 3 or 6" - {
      "transport means must not be defined" in {
        forAll(incidentCodeNot3Or6Gen) {
          incidentCode =>
            val initialAnswers = emptyUserAnswers.setValue(IncidentCodePage(incidentIndex), incidentCode)

            forAll(arbitraryIncidentAnswers(initialAnswers, incidentIndex)) {
              userAnswers =>
                val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](
                  IncidentDomain.userAnswersReader(index)
                ).run(userAnswers)

                result.value.transportMeans must not be defined
            }
        }
      }
    }

    "cannot be parsed from UserAnswer" - {

      "when a mandatory page is missing" in {

        val mandatoryPages: Seq[QuestionPage[_]] = Seq(
          IncidentCountryPage(index),
          IncidentCodePage(index),
          IncidentTextPage(index),
          AddEndorsementPage(index)
        )

        val userAnswers = emptyUserAnswers
          .setValue(IncidentCountryPage(index), country)
          .setValue(IncidentCodePage(index), incidentCode)
          .setValue(IncidentTextPage(index), incidentText)
          .setValue(AddEndorsementPage(index), arbitrary[Boolean].sample.value)

        mandatoryPages.map {
          mandatoryPage =>
            val updatedAnswers = userAnswers.removeValue(mandatoryPage)

            val result: EitherType[IncidentDomain] = UserAnswersReader[IncidentDomain](IncidentDomain.userAnswersReader(index)).run(updatedAnswers)

            result.left.value.page mustBe mandatoryPage
        }

      }

    }
  }

}
