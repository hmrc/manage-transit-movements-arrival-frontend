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

package controllers.actions

import base.SpecBase
import generators.Generators
import models.Index
import models.incident.IncidentCode
import models.journeyDomain.incident.equipment.EquipmentDomain
import models.journeyDomain.{JourneyDomainModel, UserAnswersReader}
import models.requests._
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalacheck.Gen
import pages.incident.IncidentCodePage
import pages.incident.equipment._
import pages.incident.equipment.seal.SealIdentificationNumberPage
import pages.sections.Section
import pages.sections.incident.{EquipmentSection, EquipmentsSection}
import play.api.libs.json.{JsArray, JsObject}
import play.api.mvc.Result
import repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RemoveInProgressActionSpec extends SpecBase with Generators {

  private val mockSessionRepository: SessionRepository = mock[SessionRepository]

  private class Harness[T <: JourneyDomainModel](
    array: Section[JsArray],
    indexedValue: Index => Section[JsObject]
  )(implicit userAnswersReader: Index => UserAnswersReader[T])
      extends RemoveInProgressAction[T](array, indexedValue)(mockSessionRepository) {

    def callRefine[A](
      request: DataRequest[A]
    ): Future[Either[Result, DataRequest[A]]] =
      refine(request)
  }

  "Remove in-progress action" - {

    "when array empty" - {
      "must return original request" in {
        val request = DataRequest(fakeRequest, eoriNumber, emptyUserAnswers)

        val action = new Harness[EquipmentDomain](
          EquipmentsSection(incidentIndex),
          EquipmentSection(incidentIndex, _)
        )(EquipmentDomain.userAnswersReader(incidentIndex, _))

        val futureResult = action.callRefine(request)

        whenReady(futureResult) {
          r =>
            r.value mustBe request
        }
      }
    }

    "when array has only completed items" - {
      "must return original request" in {
        when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

        val userAnswers = emptyUserAnswers
          .setValue(IncidentCodePage(index), IncidentCode("2", "test"))
          .setValue(ContainerIdentificationNumberYesNoPage(index, equipmentIndex), true)
          .setValue(ContainerIdentificationNumberPage(index, equipmentIndex), Gen.alphaNumStr.sample.value)
          .setValue(AddSealsYesNoPage(index, equipmentIndex), true)
          .setValue(SealIdentificationNumberPage(index, equipmentIndex, sealIndex), "1234")
          .setValue(AddGoodsItemNumberYesNoPage(index, equipmentIndex), false)

        val request = DataRequest(fakeRequest, eoriNumber, userAnswers)

        val action = new Harness[EquipmentDomain](
          EquipmentsSection(incidentIndex),
          EquipmentSection(incidentIndex, _)
        )(EquipmentDomain.userAnswersReader(incidentIndex, _))

        val futureResult = action.callRefine(request)

        whenReady(futureResult) {
          r =>
            r.value mustBe request
            verify(mockSessionRepository).set(eqTo(userAnswers))(any())
        }
      }
    }

    "when array has in progress items" - {
      "must strip these out and update user answers" in {
        when(mockSessionRepository.set(any())(any())).thenReturn(Future.successful(true))

        val userAnswers = emptyUserAnswers
          .setValue(IncidentCodePage(Index(0)), IncidentCode("2", "test"))
          .setValue(ContainerIdentificationNumberYesNoPage(index, Index(0)), true)
          .setValue(ContainerIdentificationNumberPage(index, Index(0)), Gen.alphaNumStr.sample.value)
          .setValue(AddSealsYesNoPage(index, Index(0)), true)
          .setValue(SealIdentificationNumberPage(index, Index(0), sealIndex), "1234")
          .setValue(AddGoodsItemNumberYesNoPage(index, Index(0)), false)
          .setValue(ContainerIdentificationNumberYesNoPage(index, Index(1)), true)
          .setValue(AddSealsYesNoPage(index, Index(1)), true)
          .setValue(AddGoodsItemNumberYesNoPage(index, Index(1)), false)

        val request = DataRequest(fakeRequest, eoriNumber, userAnswers)

        val action = new Harness[EquipmentDomain](
          EquipmentsSection(incidentIndex),
          EquipmentSection(incidentIndex, _)
        )(EquipmentDomain.userAnswersReader(incidentIndex, _))

        val futureResult = action.callRefine(request)

        val expectedAnswers = userAnswers
          .removeValue(EquipmentSection(index, Index(1)))

        whenReady(futureResult) {
          r =>
            r.value.userAnswers mustBe expectedAnswers
            verify(mockSessionRepository).set(eqTo(expectedAnswers))(any())
        }
      }
    }
  }
}
