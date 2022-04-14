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

package pages.events.transhipments

import models.TranshipmentType._
import models.domain.ContainerDomain
import models.reference.CountryCode
import models.{Index, TranshipmentType, UserAnswers}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import queries.ContainersQuery

class TranshipmentTypePageSpec extends PageBehaviours {

  "TranshipmentTypePage" - {

    beRetrievable[TranshipmentType](TranshipmentTypePage(eventIndex))

    beSettable[TranshipmentType](TranshipmentTypePage(eventIndex))

    beRemovable[TranshipmentType](TranshipmentTypePage(eventIndex))

    "cleanup" - {
      "must remove transport identity and nationality when the answer change to Different Container" in {

        forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[CountryCode]) {
          (userAnswers, transportIdentity, transportNationality) =>
            val result = userAnswers
              .setValue(TranshipmentTypePage(eventIndex), DifferentVehicle)
              .setValue(TransportIdentityPage(eventIndex), transportIdentity)
              .setValue(TransportNationalityPage(eventIndex), transportNationality)
              .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)

            result.get(TransportIdentityPage(eventIndex)) must not be defined
            result.get(TransportNationalityPage(eventIndex)) must not be defined
        }
      }

      "must remove container numbers when the answer changes to Different Vehicle" in {

        forAll(arbitrary[UserAnswers], arbitrary[ContainerDomain]) {
          (userAnswers, containerNumber) =>
            val result = userAnswers
              .setValue(TranshipmentTypePage(eventIndex), DifferentContainer)
              .setValue(ContainerNumberPage(eventIndex, Index(0)), containerNumber)
              .setValue(ContainerNumberPage(eventIndex, Index(1)), containerNumber)
              .setValue(TranshipmentTypePage(eventIndex), TranshipmentType.DifferentVehicle)

            result.get(ContainersQuery(eventIndex)) must not be defined
        }
      }

      "must remove all transhipment data when there is no answer" in {

        forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[ContainerDomain], arbitrary[CountryCode]) {
          (userAnswers, stringAnswer, container, country) =>
            val result = userAnswers
              .setValue(TransportIdentityPage(eventIndex), stringAnswer)
              .setValue(TransportNationalityPage(eventIndex), country)
              .setValue(ContainerNumberPage(eventIndex, Index(0)), container)
              .setValue(ContainerNumberPage(eventIndex, Index(1)), container)
              .removeValue(TranshipmentTypePage(eventIndex))

            result.get(TransportIdentityPage(eventIndex)) must not be defined
            result.get(TransportNationalityPage(eventIndex)) must not be defined
            result.get(ContainerNumberPage(eventIndex, Index(0))) must not be defined
            result.get(ContainerNumberPage(eventIndex, Index(1))) must not be defined
        }
      }
    }
  }
}
