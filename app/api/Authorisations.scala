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

package api

import generated.AuthorisationType01
import models.UserAnswers
import play.api.libs.functional.syntax._
import play.api.libs.json.{__, Reads}

object Authorisations {

  def transform(uA: UserAnswers): Seq[AuthorisationType01] =
    uA.data.as[Seq[AuthorisationType01]](authorisationsPath.readArray[AuthorisationType01](authorisationType01.reads))
}

object authorisationType01 {

  def reads(index: Int): Reads[AuthorisationType01] = (
    (__ \ "typeValue").read[String] and
      (__ \ "referenceNumber").read[String]
  ).apply {
    (authType, reference) =>
      AuthorisationType01(index.toString, authType, reference)
  }

}
