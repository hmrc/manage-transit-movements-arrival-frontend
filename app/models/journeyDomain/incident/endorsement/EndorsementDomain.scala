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

package models.journeyDomain.incident.endorsement

import models.Index
import models.journeyDomain.{GettableAsReaderOps, UserAnswersReader}
import pages.incident.EndorsementDatePage
import cats.implicits._

import java.time.LocalDate

case class EndorsementDomain(date: LocalDate)

object EndorsementDomain {

  def userAnswersReader(index: Index): UserAnswersReader[EndorsementDomain] =
    EndorsementDatePage(index).reader.map(EndorsementDomain(_))

}
