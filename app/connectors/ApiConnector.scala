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

package connectors

import api.Conversions
import api.Conversions.scope
import config.FrontendAppConfig
import generated.{CC007CType, MESSAGE_FROM_TRADERSequence, MessageType007, PhaseIDtype}
import models.UserAnswers
import models.journeyDomain.{ArrivalDomain, ArrivalPostTransitionDomain, UserAnswersReader}
import play.api.Logging
import play.api.http.HeaderNames
import scalaxb.DataRecord
import scalaxb.`package`.toXML
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpClient, HttpErrorFunctions, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiConnector @Inject() (httpClient: HttpClient, appConfig: FrontendAppConfig)(implicit ec: ExecutionContext) extends HttpErrorFunctions with Logging {

  private val requestHeaders = Seq(
    HeaderNames.ACCEPT       -> "application/vnd.hmrc.2.0+json",
    HeaderNames.CONTENT_TYPE -> "application/xml"
  )

  def createPayload(userAnswers: UserAnswers): Either[Object, CC007CType] =
    for {
      arrivalDomain <- UserAnswersReader[ArrivalPostTransitionDomain].run(userAnswers)
    } yield {
      val message: MESSAGE_FROM_TRADERSequence = Conversions.message
      val messageType: MessageType007          = Conversions.messageType
      val correlationIdentifier                = Conversions.correlationIdentifier
      val transitOperation                     = Conversions.transitOperation(arrivalDomain.identification, arrivalDomain.incidents.isDefined)
      val authorisations                       = Conversions.authorisations(arrivalDomain.identification.authorisations)
      val customsOfficeOfDestination           = Conversions.customsOfficeOfDestination(arrivalDomain.identification.destinationOffice)
      val traderAtDestination                  = Conversions.traderAtDestination(arrivalDomain.identification)
      val consignment                          = Conversions.consignment(arrivalDomain)

      CC007CType(
        message,
        messageType,
        correlationIdentifier,
        transitOperation,
        authorisations,
        customsOfficeOfDestination,
        traderAtDestination,
        consignment,
        attributes = Map("@PhaseID" -> DataRecord(PhaseIDtype.fromString("NCTS5.0", scope)))
      )
    }

  def submitDeclaration(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val declarationUrl = s"${appConfig.apiUrl}/movements/arrivals"

    createPayload(userAnswers) match {
      case Left(msg) => throw new BadRequestException(msg.toString)
      case Right(submissionModel) =>
        val payload: String = toXML[CC007CType](submissionModel, "ncts:CC007C", scope).toString

        println(s"\n\n\n\nPAYLOAD:  $payload \n\n\n\n")
        httpClient.POSTString(declarationUrl, payload, requestHeaders)
    }

  }
}
