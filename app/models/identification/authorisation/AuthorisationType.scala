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

package models.identification.authorisation

import models.{RadioModel, WithName}

sealed trait AuthorisationType

object AuthorisationType extends RadioModel[AuthorisationType] {

  case object ACT extends WithName("act") with AuthorisationType
  case object ACE extends WithName("ace") with AuthorisationType

  override val messageKeyPrefix: String = "identification.authorisation.authorisationType"

  val values: Seq[AuthorisationType] = Seq(
    ACT,
    ACE
  )
}
