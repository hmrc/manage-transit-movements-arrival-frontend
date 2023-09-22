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

package services

import base.SpecBase
import connectors.ReferenceDataConnector
import models.reference.{Identification, IncidentCode}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IncidentCodeServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new ReferenceDataDynamicRadioService(mockRefDataConnector)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "IncidentCodesService" - {

    "getIncidentCodes" - {
      val incidentCode1: IncidentCode = IncidentCode("2", "Test2")
      val incidentCode2: IncidentCode = IncidentCode("1", "Test1")

      "must return a list of sorted incidentCodes" in {

        when(mockRefDataConnector.getIncidentCodes()(any(), any()))
          .thenReturn(Future.successful(Seq(incidentCode1, incidentCode2)))

        service.getIncidentCodes().futureValue mustBe Seq(incidentCode2, incidentCode1)

        verify(mockRefDataConnector).getIncidentCodes()(any(), any())
      }
    }

    "getTransportIdentifications" - {
      val transportId1: Identification = Identification("11", "Name of the sea-going vessel")
      val transportId2: Identification = Identification("10", "IMO Ship Identification Number")

      "must return a list of sorted transport identifications" in {

        when(mockRefDataConnector.getTransportIdentifications()(any(), any()))
          .thenReturn(Future.successful(Seq(transportId1, transportId2)))

        service.getTransportIdentifications().futureValue mustBe Seq(transportId2, transportId1)

        verify(mockRefDataConnector).getTransportIdentifications()(any(), any())
      }
    }
  }
}
