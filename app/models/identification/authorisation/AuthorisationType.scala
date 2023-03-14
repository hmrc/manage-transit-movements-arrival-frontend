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

package models.identification.authorisation

import models.{EnumerableType, Radioable, WithName}

sealed trait AuthorisationType extends Radioable[AuthorisationType] {

  override val messageKeyPrefix: String = AuthorisationType.messageKeyPrefix

  def asString(f: String => AuthorisationType => String): String =
    f(AuthorisationType.prefixForDisplay)(this)
}

object AuthorisationType extends EnumerableType[AuthorisationType] {

  case object ACT extends WithName("ACT") with AuthorisationType
  case object ACE extends WithName("ACE") with AuthorisationType

  val messageKeyPrefix: String = "identification.authorisation.authorisationType"
  val prefixForDisplay: String = s"$messageKeyPrefix.forDisplay"

  val values: Seq[AuthorisationType] = Seq(
    ACT,
    ACE
  )
}
