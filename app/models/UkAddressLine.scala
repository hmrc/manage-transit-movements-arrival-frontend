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

package models

import models.domain.StringFieldRegex.{stringFieldRegex, ukPostCodeRegex}
import play.api.i18n.Messages

import scala.util.matching.Regex

sealed trait UkAddressLine {
  val field: String
  def arg(implicit messages: Messages): String = messages(s"address.$field")
}

object UkAddressLine {

  case object BuildingAndStreet extends UkAddressLine {
    override val field: String = "buildingAndStreet"
    val length: Int            = 35
    val regex: Regex           = stringFieldRegex
  }

  case object City extends UkAddressLine {
    override val field: String = "city"
    val length: Int            = 35
    val regex: Regex           = stringFieldRegex
  }

  case object PostCode extends UkAddressLine {
    override val field: String = "postcode"
    val length: Int            = 9
    val regex: Regex           = ukPostCodeRegex
  }
}
