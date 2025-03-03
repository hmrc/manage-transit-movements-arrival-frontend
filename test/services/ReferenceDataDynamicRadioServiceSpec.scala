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
import cats.data.NonEmptySet
import config.Constants.LocationType.*
import connectors.ReferenceDataConnector
import models.reference.{QualifierOfIdentification, TypeOfLocation}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.collection.immutable.Seq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ReferenceDataDynamicRadioServiceSpec extends SpecBase with BeforeAndAfterEach {

  private val mockRefDataConnector: ReferenceDataConnector       = mock[ReferenceDataConnector]
  private val service                                            = new ReferenceDataDynamicRadioService(mockRefDataConnector)
  private val unlocode: QualifierOfIdentification                = QualifierOfIdentification("U", "UN/LOCODE")
  private val customsOfficeIdentifier: QualifierOfIdentification = QualifierOfIdentification("V", "Customs office identifier")
  private val coordinates: QualifierOfIdentification             = QualifierOfIdentification("W", "GPS coordinates")
  private val eoriNumberIdentifier: QualifierOfIdentification    = QualifierOfIdentification("X", "EORI number")
  private val authNumber: QualifierOfIdentification              = QualifierOfIdentification("Y", "Authorisation number")
  private val address: QualifierOfIdentification                 = QualifierOfIdentification("Z", "Free text")

  private val ids = NonEmptySet.of(unlocode, customsOfficeIdentifier, coordinates, eoriNumberIdentifier, authNumber, address)

  override def beforeEach(): Unit = {
    reset(mockRefDataConnector)
    super.beforeEach()
  }

  "ReferenceDataDynamicRadioService" - {

    "getTypesOfLocation" - {

      val typeOfLocation1: TypeOfLocation = TypeOfLocation("C", "TestA")
      val typeOfLocation2: TypeOfLocation = TypeOfLocation("B", "TestB")
      val typeOfLocation3: TypeOfLocation = TypeOfLocation("A", "TestC")
      val typesOfLocation                 = NonEmptySet.of(typeOfLocation1, typeOfLocation2, typeOfLocation3)

      "must return a list of sorted TypeOfLocation filtered -without B" in {

        when(mockRefDataConnector.getTypesOfLocation()(any(), any()))
          .thenReturn(Future.successful(Right(typesOfLocation)))

        service.getTypesOfLocation().futureValue mustBe Seq(typeOfLocation3, typeOfLocation1)

      }
    }

    "getIdentifications" - {

      "must return list of sorted transport identifications" - {

        "must show an filtered list when TypeOfLocation is DesignatedLocation" in {
          when(mockRefDataConnector.getIdentifications()(any(), any()))
            .thenReturn(Future.successful(Right(ids)))

          service.getIdentifications(TypeOfLocation(DesignatedLocation, "test")).futureValue mustBe
            Seq(unlocode, customsOfficeIdentifier)

          verify(mockRefDataConnector).getIdentifications()(any(), any())
        }

        "must show an filtered list when TypeOfLocation is Approved place" in {
          when(mockRefDataConnector.getIdentifications()(any(), any()))
            .thenReturn(Future.successful(Right(ids)))

          service.getIdentifications(TypeOfLocation(ApprovedPlace, "test")).futureValue mustBe
            Seq(
              unlocode,
              coordinates,
              eoriNumberIdentifier,
              address
            )

          verify(mockRefDataConnector).getIdentifications()(any(), any())
        }

        "must show an filtered list when TypeOfLocation is Other location" in {
          when(mockRefDataConnector.getIdentifications()(any(), any()))
            .thenReturn(Future.successful(Right(ids)))

          service.getIdentifications(TypeOfLocation(Other, "test")).futureValue mustBe Seq(unlocode, coordinates, address)

          verify(mockRefDataConnector).getIdentifications()(any(), any())
        }
      }
    }

  }
}
