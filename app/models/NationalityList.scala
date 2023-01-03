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

package models

import models.reference.Nationality
import play.api.libs.json.{Format, Json}

case class NationalityList(nationalities: Seq[Nationality]) {

  def getAll: Seq[Nationality] =
    nationalities

  def getNationality(code: String): Option[Nationality] =
    nationalities.find(_.code == code)

  override def equals(obj: Any): Boolean = obj match {
    case x: NationalityList => x.getAll == getAll
    case _                  => false
  }

  def sort: NationalityList = this.copy(nationalities = nationalities.sortBy(_.desc.toLowerCase))

}

object NationalityList {

  implicit val format: Format[NationalityList] = Json.format[NationalityList]

  def apply(nationalities: Seq[Nationality]): NationalityList =
    new NationalityList(nationalities)
}
