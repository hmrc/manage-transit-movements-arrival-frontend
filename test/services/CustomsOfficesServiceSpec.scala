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
import config.FrontendAppConfig
import connectors.ReferenceDataConnector
import models.SelectableList
import models.reference.CustomsOffice
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CustomsOfficesServiceSpec extends SpecBase {

  private val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]
  private val mockFrontendAppConfig: FrontendAppConfig     = mock[FrontendAppConfig]

  private val gbCustomsOffice1: CustomsOffice = CustomsOffice("1", "BOSTON", None, "GB")
  private val gbCustomsOffice2: CustomsOffice = CustomsOffice("2", "Appledore", None, "GB")
  private val xiCustomsOffice1: CustomsOffice = CustomsOffice("3", "Belfast", None, "XI")
  private val xiCustomsOffice2: CustomsOffice = CustomsOffice("4", "Blah", None, "XI")

  private val countriesOfDestination: Seq[String] = Seq("GB", "XI")

  val customsOffices: NonEmptySet[CustomsOffice] = NonEmptySet.of(gbCustomsOffice1, gbCustomsOffice2, xiCustomsOffice1, xiCustomsOffice2)

  private val service = new CustomsOfficesService(mockRefDataConnector, mockFrontendAppConfig)

  "CustomsOfficesService" - {

    "must return a list of GB and NI customs offices" in {

      when(mockRefDataConnector.getCustomsOfficesForCountry(any())(any(), any()))
        .thenReturn(Future.successful(Right(customsOffices)))

      when(mockFrontendAppConfig.countriesOfDestination).thenReturn(countriesOfDestination)

      service.getCustomsOfficesOfArrival.futureValue mustEqual
        SelectableList(Seq(gbCustomsOffice2, xiCustomsOffice1, xiCustomsOffice2, gbCustomsOffice1))

      val varargsCaptor: ArgumentCaptor[Seq[String]] = ArgumentCaptor.forClass(classOf[Seq[String]])
      verify(mockRefDataConnector).getCustomsOfficesForCountry(varargsCaptor.capture()*)(any(), any())
      varargsCaptor.getValue mustEqual countriesOfDestination
    }

  }
}
