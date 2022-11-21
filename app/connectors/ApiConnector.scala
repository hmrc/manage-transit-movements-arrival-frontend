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
import config.FrontendAppConfig
import generated.TransitOperationType02
import models.UserAnswers
import play.api.Logging
import play.api.http.HeaderNames
import uk.gov.hmrc.http.{BadRequestException, HeaderCarrier, HttpClient, HttpErrorFunctions, HttpResponse}
import scalaxb.`package`.toXML

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ApiConnector @Inject() (httpClient: HttpClient, appConfig: FrontendAppConfig)(implicit ec: ExecutionContext) extends HttpErrorFunctions with Logging {

  private val requestHeaders = Seq(
    HeaderNames.ACCEPT       -> "application/vnd.hmrc.2.0+json",
    HeaderNames.CONTENT_TYPE -> "application/xml"
  )

  // TODO - Implement as per declarations
  def createSubmission(userAnswers: UserAnswers): Either[String, String] =
    for {
      transitOperation <- Conversions.transitOperation(userAnswers)
    } yield payloadXml(transitOperation)

  // TODO - build out example submission
  def payloadXml(transitOperation: TransitOperationType02): String =
    (<ncts:CC007C PhaseID="NCTS5.0" xmlns:ncts="http://ncts.dgtaxud.ec">
      <messageRecipient>token</messageRecipient>
      <preparationDateAndTime>2007-10-26T07:36:28</preparationDateAndTime>
      <messageIdentification>token</messageIdentification>
      <messageType>CC007C</messageType>
      <correlationIdentifier>token</correlationIdentifier>
      {toXML[TransitOperationType02](transitOperation, "TransitOperation", generated.defaultScope)}
      <Authorisation>
        <sequenceNumber>123</sequenceNumber>
        <type>3344</type>
        <referenceNumber>token</referenceNumber>
      </Authorisation>
      <CustomsOfficeOfDestinationActual>
        <referenceNumber>GB123456</referenceNumber>
      </CustomsOfficeOfDestinationActual>
      <TraderAtDestination>
        <identificationNumber>ezv3Z</identificationNumber>
        <communicationLanguageAtDestination>sa</communicationLanguageAtDestination>
      </TraderAtDestination>
      <Consignment>
        <LocationOfGoods>
          <typeOfLocation>A</typeOfLocation>
          <qualifierOfIdentification>A</qualifierOfIdentification>
          <authorisationNumber>token</authorisationNumber>
          <additionalIdentifier>1234</additionalIdentifier>
          <UNLocode>token</UNLocode>
          <CustomsOffice>
            <referenceNumber>AB123456</referenceNumber>
          </CustomsOffice>
          <EconomicOperator>
            <identificationNumber>ezv3Z</identificationNumber>
          </EconomicOperator>
          <Address>
            <streetAndNumber>token</streetAndNumber>
            <postcode>token</postcode>
            <city>token</city>
            <country>GB</country>
          </Address>
          <PostcodeAddress>
            <houseNumber>token</houseNumber>
            <postcode>token</postcode>
            <country>SA</country>
          </PostcodeAddress>
          <ContactPerson>
            <name>token</name>
            <phoneNumber>token</phoneNumber>
            <eMailAddress>sandeep@gmail.com</eMailAddress>
          </ContactPerson>
        </LocationOfGoods>
        <Incident>
          <sequenceNumber>12345</sequenceNumber>
          <code>1</code>
          <text>token</text>
          <Endorsement>
            <date>2022-07-02</date>
            <authority>token</authority>
            <place>token</place>
            <country>GB</country>
          </Endorsement>
          <Location>
            <qualifierOfIdentification>A</qualifierOfIdentification>
            <UNLocode>token</UNLocode>
            <country>SA</country>
            <Address>
              <streetAndNumber>token</streetAndNumber>
              <postcode>token</postcode>
              <city>token</city>
            </Address>
          </Location>
          <TransportEquipment>
            <sequenceNumber>12345</sequenceNumber>
            <containerIdentificationNumber>ezv3Z</containerIdentificationNumber>
            <numberOfSeals>2345</numberOfSeals>
            <Seal>
              <sequenceNumber>12345</sequenceNumber>
              <identifier>token</identifier>
            </Seal>
            <GoodsReference>
              <sequenceNumber>12345</sequenceNumber>
              <declarationGoodsItemNumber>12</declarationGoodsItemNumber>
            </GoodsReference>
          </TransportEquipment>
          <Transhipment>
            <containerIndicator>0</containerIndicator>
            <TransportMeans>
              <typeOfIdentification>12</typeOfIdentification>
              <identificationNumber>ezv3Z</identificationNumber>
              <nationality>GB</nationality>
            </TransportMeans>
          </Transhipment>
        </Incident>
      </Consignment>
    </ncts:CC007C>).mkString

  def submitDeclaration(userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val declarationUrl = s"${appConfig.apiUrl}/movements/arrivals"

    createSubmission(userAnswers) match {
      case Left(msg)    => throw new BadRequestException(msg)
      case Right(value) => httpClient.POSTString(declarationUrl, value, requestHeaders)
    }

  }

}
