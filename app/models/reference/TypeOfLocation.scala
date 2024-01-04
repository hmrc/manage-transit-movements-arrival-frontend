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

import models.{DynamicEnumerableType, Radioable}
import play.api.libs.json.{Format, Json}

case class TypeOfLocation(`type`: String, description: String) extends Radioable[TypeOfLocation] {
  override def toString: String         = description
  override val messageKeyPrefix: String = TypeOfLocation.messageKeyPrefix
  override val code: String             = `type`
}

object TypeOfLocation extends DynamicEnumerableType[TypeOfLocation] {
  implicit val format: Format[TypeOfLocation] = Json.format[TypeOfLocation]

  val messageKeyPrefix: String = "locationOfGoods.typeOfLocation"

}