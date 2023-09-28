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

package pages.incident.transportMeans

import generators.Generators
import models.reference.{Identification, Nationality}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.behaviours.PageBehaviours

class IdentificationPageSpec extends PageBehaviours with ScalaCheckPropertyChecks with Generators {

  "IdentificationPage" - {

    beRetrievable[Identification](IdentificationPage(index))

    beSettable[Identification](IdentificationPage(index))

    beRemovable[Identification](IdentificationPage(index))

    "cleanup" - {
      "when answer changes" - {
        "must remove id number and country" in {
          forAll(arbitrary[Identification], Gen.alphaNumStr, arbitrary[Nationality]) {
            (identificationType, identificationNumber, country) =>
              val userAnswers = emptyUserAnswers
                .setValue(IdentificationPage(incidentIndex), identificationType)
                .setValue(IdentificationNumberPage(incidentIndex), identificationNumber)
                .setValue(TransportNationalityPage(incidentIndex), country)

              forAll(arbitrary[Identification].retryUntil(_ != identificationType)) {
                differentAnswer =>
                  val result = userAnswers.setValue(IdentificationPage(incidentIndex), differentAnswer)

                  result.get(IdentificationNumberPage(incidentIndex)) must not be defined
                  result.get(TransportNationalityPage(incidentIndex)) must not be defined
              }
          }
        }
      }

      "when answer doesn't change" - {
        "must not remove id number and country" in {
          forAll(arbitrary[Identification], Gen.alphaNumStr, arbitrary[Nationality]) {
            (identificationType, identificationNumber, country) =>
              val userAnswers = emptyUserAnswers
                .setValue(IdentificationPage(incidentIndex), identificationType)
                .setValue(IdentificationNumberPage(incidentIndex), identificationNumber)
                .setValue(TransportNationalityPage(incidentIndex), country)

              val result = userAnswers.setValue(IdentificationPage(incidentIndex), identificationType)

              result.get(IdentificationNumberPage(incidentIndex)) must be(defined)
              result.get(TransportNationalityPage(incidentIndex)) must be(defined)
          }
        }
      }
    }
  }
}
