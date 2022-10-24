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

package utils

import config.FrontendAppConfig
import models.identification.ProcedureType
import models.journeyDomain.identification.AuthorisationDomain
import models.locationOfGoods.{QualifierOfIdentification, TypeOfLocation}
import models.reference.{CustomsOffice, UnLocode}
import models.{Coordinates, Index, InternationalAddress, Mode, PostalCodeAddress, UserAnswers}
import pages.identification.{DestinationOfficePage, IdentificationNumberPage, IsSimplifiedProcedurePage}
import pages.locationOfGoods._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow

class CheckArrivalsHelper(
  userAnswers: UserAnswers,
  mode: Mode
)(implicit messages: Messages, config: FrontendAppConfig)
    extends AnswersHelper(userAnswers, mode) {

  def movementReferenceNumber: SummaryListRow = buildRow(
    prefix = "movementReferenceNumber",
    answer = formatAsText(mrn),
    id = None,
    call = controllers.identification.routes.MovementReferenceNumberController.onPageLoad()
  )

  def destinationOffice: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = DestinationOfficePage,
    formatAnswer = formatAsText,
    prefix = "identification.destinationOffice",
    id = Some("change-destination-office")
  )

  def identificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "identification.identificationNumber",
    id = Some("change-identification-number")
  )

  def isSimplified: Option[SummaryListRow] = getAnswerAndBuildRow[ProcedureType](
    page = IsSimplifiedProcedurePage,
    formatAnswer = formatEnumAsText(ProcedureType.messageKeyPrefix),
    prefix = "identification.isSimplifiedProcedure",
    id = Some("change-is-simplified-procedure")
  )

  def authorisation(index: Index): Option[SummaryListRow] = getAnswerAndBuildSectionRow[AuthorisationDomain](
    formatAnswer = formatAsText,
    prefix = "identification.authorisation",
    id = Some(s"change-authorisation-${index.display}"),
    args = index.display
  )(AuthorisationDomain.userAnswersReader(index))

  def locationType: Option[SummaryListRow] = getAnswerAndBuildRow[TypeOfLocation](
    page = TypeOfLocationPage,
    formatAnswer = formatEnumAsText(TypeOfLocation.messageKeyPrefix),
    prefix = "locationOfGoods.typeOfLocation",
    id = Some("type-of-location")
  )

  def qualifierOfIdentification: Option[SummaryListRow] = getAnswerAndBuildRow[QualifierOfIdentification](
    page = QualifierOfIdentificationPage,
    formatAnswer = formatEnumAsText(QualifierOfIdentification.messageKeyPrefix),
    prefix = "locationOfGoods.qualifierOfIdentification",
    id = Some("qualifier-of-identification")
  )

  def customsOfficeIdentifier: Option[SummaryListRow] = getAnswerAndBuildRow[CustomsOffice](
    page = CustomsOfficePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.customsOffice",
    id = Some("customs-office")
  )

  def locationOfGoodsIdentificationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = IdentificationNumberPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.identificationNumber",
    id = Some("identification-number")
  )

  def authorisationNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AuthorisationNumberPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.authorisationNumber",
    id = Some("authorisation-number")
  )

  def coordinates: Option[SummaryListRow] = getAnswerAndBuildRow[Coordinates](
    page = CoordinatesPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.coordinates",
    id = Some("coordinates")
  )

  def unLocode: Option[SummaryListRow] = getAnswerAndBuildRow[UnLocode](
    page = UnlocodePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.unLocode",
    id = Some("un-locode")
  )

  def address: Option[SummaryListRow] = getAnswerAndBuildRow[InternationalAddress](
    page = InternationalAddressPage,
    formatAnswer = formatAsAddress,
    prefix = "locationOfGoods.internationalAddress",
    id = Some("international-address")
  )

  def postalCode: Option[SummaryListRow] = getAnswerAndBuildRow[PostalCodeAddress](
    page = AddressPage,
    formatAnswer = formatAsPostalCodeAddress,
    prefix = "locationOfGoods.address",
    id = Some("address")
  )

  def additionalIdentifierYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddAdditionalIdentifierPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "locationOfGoods.addAdditionalIdentifier",
    id = Some("add-additional-identifier")
  )

  def additionalIdentifier: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = AdditionalIdentifierPage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.additionalIdentifier",
    id = Some("additional-identifier")
  )

  def contactYesNo: Option[SummaryListRow] = getAnswerAndBuildRow[Boolean](
    page = AddContactPersonPage,
    formatAnswer = formatAsYesOrNo,
    prefix = "locationOfGoods.addContactPerson",
    id = Some("add-contact-person")
  )

  def contactName: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContactPersonNamePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.contactPersonName",
    id = Some("contact-person-name")
  )

  def contactPhoneNumber: Option[SummaryListRow] = getAnswerAndBuildRow[String](
    page = ContactPersonTelephonePage,
    formatAnswer = formatAsText,
    prefix = "locationOfGoods.contactPersonTelephone",
    id = Some("contact-person-telephone")
  )

}