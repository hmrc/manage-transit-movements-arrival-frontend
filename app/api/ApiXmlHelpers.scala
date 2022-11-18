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

package api

import generated.{Number0, Number1}
import javax.xml.datatype.DatatypeFactory

object ApiXmlHelpers {

  def toDate(date: String) =
    DatatypeFactory
      .newInstance()
      .newXMLGregorianCalendar(date)

  def boolToFlag(x: Boolean) = x match {
    case true => Number1
    case _    => Number0
  }
}
