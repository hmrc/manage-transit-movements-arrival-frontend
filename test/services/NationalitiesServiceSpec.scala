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
import connectors.ReferenceDataConnector
import models.SelectableList
import models.reference.Nationality
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.BeforeAndAfterEach

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class NationalitiesServiceSpec extends SpecBase with BeforeAndAfterEach {

  private lazy val mockRefDataConnector: ReferenceDataConnector = mock[ReferenceDataConnector]

  private val nationality1: Nationality = Nationality("Code1", "Desc1")
  private val nationality2: Nationality = Nationality("Code2", "Desc2")
  private val nationality3: Nationality = Nationality("Code3", "Desc3")

  private val nationalities: NonEmptySet[Nationality] = NonEmptySet.of(nationality2, nationality1, nationality3)

  private val service: NationalitiesService = new NationalitiesService(mockRefDataConnector)

  "NationalitiesService" - {

    "getNationalities" - {

      "must return a list of sorted nationalities" in {

        when(mockRefDataConnector.getNationalities()(any(), any()))
          .thenReturn(Future.successful(Right(nationalities)))

        service.getNationalities().futureValue mustEqual
          SelectableList(Seq(nationality1, nationality2, nationality3))

        verify(mockRefDataConnector).getNationalities()(any(), any())

      }
    }
  }
}
