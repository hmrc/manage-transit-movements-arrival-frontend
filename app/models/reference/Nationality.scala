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

package models.reference

import cats.Order
import config.FrontendAppConfig
import models.Selectable
import play.api.libs.functional.syntax.*
import play.api.libs.json.{__, Format, Json, Reads}

case class Nationality(code: String, description: String) extends Selectable {

  override def toString: String = s"$description - $code"

  override val value: String = code
}

object Nationality {

  def reads(config: FrontendAppConfig): Reads[Nationality] =
    if (config.phase6Enabled) {
      (
        (__ \ "key").read[String] and
          (__ \ "value").read[String]
      )(Nationality.apply)
    } else {
      Json.reads[Nationality]
    }

  implicit val format: Format[Nationality] = Json.format[Nationality]

  implicit val order: Order[Nationality] = (x: Nationality, y: Nationality) => (x, y).compareBy(_.description, _.code)
}
