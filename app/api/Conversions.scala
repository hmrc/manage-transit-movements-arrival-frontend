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

import generated._
import models.UserAnswers
import models.identification.ProcedureType
import org.joda.time.DateTime
import pages.identification.IsSimplifiedProcedurePage
import pages.incident.IncidentFlagPage
import pages.sections.identification.AuthorisationsSection

object Conversions {

  def message: MESSAGE_FROM_TRADERSequence =
    MESSAGE_FROM_TRADERSequence(
      Some("manage-transit-movements-arrival-frontend"),
      MESSAGE_1Sequence(
        "TODO ???",
        ApiXmlHelpers.toDate(DateTime.now().toString()),
        "TODO ???"
      )
    )

  def messageType: MessageType007 = MessageType007.fromString("CC007C", generated.defaultScope)
  def correlationIdentifier       = CORRELATION_IDENTIFIERSequence(Some("TODO ???"))

  def transitOperation(userAnswers: UserAnswers): Either[String, TransitOperationType02] =
    for {
      sp           <- userAnswers.getAsEither(IsSimplifiedProcedurePage)
      incidentFlag <- userAnswers.getAsEither(IncidentFlagPage)
    } yield TransitOperationType02(
      userAnswers.mrn.toString,
      arrivalNotificationDateAndTime = ApiXmlHelpers.toDate(DateTime.now().toString()),
      simplifiedProcedure = ApiXmlHelpers.boolToFlag(sp match {
        case ProcedureType.Simplified => true
        case _                        => false
      }),
      incidentFlag = ApiXmlHelpers.boolToFlag(incidentFlag)
    )

  def authorisations(userAnswers: UserAnswers): Either[String, Seq[AuthorisationType01]] = {
    for {
      // Is JsArray - How do we read a section to build a Seq[AuthorisationType01]?
      authSection <- userAnswers.getAsEither(AuthorisationsSection)
    } yield ???
  }

}
