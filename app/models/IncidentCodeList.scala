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

import models.reference.IncidentCode

case class IncidentCodeList(incidentCodes: Seq[IncidentCode]) {

  def getAll: Seq[IncidentCode] = incidentCodes

  def getIncidentCode(code: String): Option[IncidentCode] = incidentCodes.find(_.code == code)

  override def equals(obj: Any): Boolean = obj match {
    case x: IncidentCodeList => x.getAll == getAll
    case _                   => false
  }

}

object IncidentCodeList {

  def apply(incidentCodes: Seq[IncidentCode]): IncidentCodeList =
    new IncidentCodeList(incidentCodes)
}
