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
import models.incident.IncidentCode
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IncidentCodeServiceSpec extends SpecBase with BeforeAndAfterEach {

  val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  val service                                      = new IncidentCodeService(mockRefDataConnector)

  val incidentCode1: IncidentCode = IncidentCode("A", "Test1")
  val incidentCode2: IncidentCode = IncidentCode("B", "Test2")
  val incidentCodes               = Seq(incidentCode1, incidentCode2)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "IncidentCodesService" - {

    "getIncidentCodes" - {
      "must return a list of sorted incidentCodes" in {

        when(mockRefDataConnector.getIncidentCodes()(any(), any()))
          .thenReturn(Future.successful(incidentCodes))

        service.getIncidentCodes().futureValue mustBe incidentCodes

        verify(mockRefDataConnector).getIncidentCodes()(any(), any())
      }
    }
  }
}
